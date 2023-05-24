package sigma.semantics.expressions

import sigma.Computation
import sigma.TypeScope
import sigma.evaluation.values.Symbol
import sigma.semantics.DeclarationScope
import sigma.semantics.SemanticError
import sigma.semantics.types.IllType
import sigma.semantics.types.TupleType
import sigma.semantics.types.Type
import sigma.syntax.expressions.FieldReadTerm

class FieldRead(
    override val term: FieldReadTerm,
    val subject: Expression,
) : Expression() {

    sealed interface InferredSubjectTypeOutcome

    data class InferredSubjectTypeResult(
        val subjectType: TupleType,
    ) : InferredSubjectTypeOutcome

    data class InvalidSubjectTypeError(
        val invalidSubjectType: Type,
    ) : InferredSubjectTypeOutcome, SemanticError

    sealed interface InferredFieldTypeOutcome

    data class InferredFieldTypeResult(
        val fieldType: Type,
    ) : InferredFieldTypeOutcome

    data class MissingFieldError(
        val subjectType: TupleType,
        val missingFieldName: Symbol,
    ) : InferredFieldTypeOutcome, SemanticError

    object InferredFieldTypeAbort : InferredFieldTypeOutcome

    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: FieldReadTerm,
        ): FieldRead = FieldRead(
            term = term,
            subject = Expression.build(
                typeScope = typeScope,
                declarationScope = declarationScope,
                term = term.subject,
            ),
        )
    }

    private val fieldName: Symbol
        get() = term.fieldName

    private val inferredSubjectTypeOutcome: Computation<InferredSubjectTypeOutcome> =
        subject.inferredType.thenJust { subjectType ->
            val validSubjectType = subjectType as? TupleType

            if (validSubjectType != null) {
                InferredSubjectTypeResult(
                    subjectType = validSubjectType,
                )
            } else {
                InvalidSubjectTypeError(
                    invalidSubjectType = subjectType,
                )
            }
        }

    private val inferredFieldTypeOutcome: Computation<InferredFieldTypeOutcome> = inferredSubjectTypeOutcome.thenJust {
        when (it) {
            is InferredSubjectTypeResult -> {
                val subjectType = it.subjectType

                val fieldType = subjectType.getFieldTypeByName(key = fieldName)

                if (fieldType != null) {
                    InferredFieldTypeResult(
                        fieldType = fieldType,
                    )
                } else {
                    MissingFieldError(
                        subjectType = subjectType,
                        missingFieldName = fieldName,
                    )
                }
            }

            is InvalidSubjectTypeError -> InferredFieldTypeAbort
        }
    }

    override val inferredType: Computation<Type> by lazy {
        inferredFieldTypeOutcome.thenJust {
            if (it is InferredFieldTypeResult) {
                it.fieldType
            } else {
                IllType
            }
        }
    }

    override val errors: Set<SemanticError> by lazy {
        setOfNotNull(
            inferredSubjectTypeOutcome.value as? InvalidSubjectTypeError,
            inferredFieldTypeOutcome.value as? MissingFieldError,
        )
    }
}
