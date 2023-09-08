package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadTerm

class FieldRead(
    override val outerScope: StaticScope,
    override val term: FieldReadTerm,
    val subject: Expression,
) : Expression() {

    sealed interface InferredSubjectTypeOutcome

    data class InferredSubjectTypeResult(
        val subjectType: UnorderedTupleType,
    ) : InferredSubjectTypeOutcome

    data class InvalidSubjectTypeError(
        override val location: SourceLocation?,
        val invalidSubjectType: Type,
    ) : InferredSubjectTypeOutcome, SemanticError

    sealed interface InferredFieldTypeOutcome

    data class InferredFieldTypeResult(
        val fieldType: Type,
    ) : InferredFieldTypeOutcome

    data class MissingFieldError(
        override val location: SourceLocation?,
        val subjectType: UnorderedTupleType,
        val missingFieldName: Symbol,
    ) : InferredFieldTypeOutcome, SemanticError

    data object InferredFieldTypeAbort : InferredFieldTypeOutcome

    companion object {
        fun build(
            outerScope: StaticScope,
            term: FieldReadTerm,
        ): FieldRead = FieldRead(
            outerScope = outerScope,
            term = term,
            subject = Expression.build(
                outerScope = outerScope,
                term = term.subject,
            ),
        )
    }

    private val fieldName: Symbol
        get() = term.fieldName

    override val analyzedExpression: Thunk<AnalyzedExpression> by lazy {
        subject.analyzedExpression.thenJust {
            analyzeWithSubject(analyzedSubject = it)
        }
    }

    private fun analyzeWithSubject(
        analyzedSubject: AnalyzedExpression,
    ): AnalyzedExpression {
        return if (analyzedSubject is AcceptableExpression) {
            val subjectType = analyzedSubject.inferredType

            if (subjectType is UnorderedTupleType) {
                analyzeWithUnorderedTupleSubject(
                    subjectType = subjectType,
                    classifiedSubject = analyzedSubject.classifiedExpression,
                )
            } else {
                object : UnacceptableExpression() {
                    override val criticalErrors: Set<SemanticError> = setOf(
                        InvalidSubjectTypeError(
                            location = subject.location,
                            invalidSubjectType = subjectType,
                        )
                    )
                }
            }
        } else {
            TransitiveUnacceptableExpression
        }
    }

    private fun analyzeWithUnorderedTupleSubject(
        subjectType: UnorderedTupleType,
        classifiedSubject: ClassifiedExpression?,
    ): AnalyzedExpression {
        val fieldType = subjectType.getFieldType(key = fieldName)

        return if (fieldType != null) {
            object : AcceptableExpression() {
                override val inferredType: Type = fieldType

                override val classifiedExpression: ClassifiedExpression? = classifiedSubject?.wrapOf { subjectValue ->
                    if (subjectValue !is DictValue) throw IllegalStateException("Subject $subjectValue is not a dict")

                    subjectValue.read(key = fieldName)!!
                }
            }
        } else {
            object : UnacceptableExpression() {
                override val criticalErrors: Set<SemanticError> = setOf(
                    MissingFieldError(
                        location = term.location,
                        subjectType = subjectType,
                        missingFieldName = fieldName,
                    )
                )
            }
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = subject.bind(
        dynamicScope = dynamicScope,
    ).thenDo { subjectValue ->
        if (subjectValue !is DictValue) throw IllegalStateException("Subject $subjectValue is not a dict")

        subjectValue.apply(
            argument = fieldName,
        )
    }

    override val subExpressions: Set<Expression> = setOf(subject)
}
