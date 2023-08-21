package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value
import sigma.semantics.SemanticError
import sigma.semantics.StaticScope
import sigma.semantics.types.IllType
import sigma.semantics.types.Type
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.FieldReadSourceTerm

class FieldRead(
    override val term: FieldReadSourceTerm,
    val subject: Expression,
) : Expression() {

    sealed interface InferredSubjectTypeOutcome

    data class InferredSubjectTypeResult(
        val subjectType: UnorderedTupleType,
    ) : InferredSubjectTypeOutcome

    data class InvalidSubjectTypeError(
        override val location: SourceLocation,
        val invalidSubjectType: Type,
    ) : InferredSubjectTypeOutcome, SemanticError

    sealed interface InferredFieldTypeOutcome

    data class InferredFieldTypeResult(
        val fieldType: Type,
    ) : InferredFieldTypeOutcome

    data class MissingFieldError(
        override val location: SourceLocation,
        val subjectType: UnorderedTupleType,
        val missingFieldName: Symbol,
    ) : InferredFieldTypeOutcome, SemanticError

    object InferredFieldTypeAbort : InferredFieldTypeOutcome

    companion object {
        fun build(
            declarationScope: StaticScope,
            term: FieldReadSourceTerm,
        ): FieldRead = FieldRead(
            term = term,
            subject = Expression.build(
                declarationScope = declarationScope,
                term = term.subject,
            ),
        )
    }

    private val fieldName: Symbol
        get() = term.fieldName

    private val inferredSubjectTypeOutcome: Thunk<InferredSubjectTypeOutcome> =
        subject.inferredType.thenJust { subjectType ->
            val validSubjectType = subjectType as? UnorderedTupleType

            if (validSubjectType != null) {
                InferredSubjectTypeResult(
                    subjectType = validSubjectType,
                )
            } else {
                InvalidSubjectTypeError(
                    location = subject.location,
                    invalidSubjectType = subjectType,
                )
            }
        }

    private val inferredFieldTypeOutcome: Thunk<InferredFieldTypeOutcome> = inferredSubjectTypeOutcome.thenJust {
        when (it) {
            is InferredSubjectTypeResult -> {
                val subjectType = it.subjectType

                val fieldType = subjectType.getFieldType(key = fieldName)

                if (fieldType != null) {
                    InferredFieldTypeResult(
                        fieldType = fieldType,
                    )
                } else {
                    MissingFieldError(
                        location = term.location,
                        subjectType = subjectType,
                        missingFieldName = fieldName,
                    )
                }
            }

            is InvalidSubjectTypeError -> InferredFieldTypeAbort
        }
    }

    override val inferredType: Thunk<Type> by lazy {
        inferredFieldTypeOutcome.thenJust {
            if (it is InferredFieldTypeResult) {
                it.fieldType
            } else {
                IllType
            }
        }
    }

    override fun bind(scope: Scope): Thunk<Value> = subject.bind(
        scope = scope,
    ).thenDo { subjectValue ->
        if (subjectValue !is DictValue) throw IllegalStateException("Subject $subjectValue is not a dict")

        subjectValue.apply(
            argument = fieldName,
        )
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredSubjectTypeOutcome.value as? InvalidSubjectTypeError,
            inferredFieldTypeOutcome.value as? MissingFieldError,
        )
    }
}
