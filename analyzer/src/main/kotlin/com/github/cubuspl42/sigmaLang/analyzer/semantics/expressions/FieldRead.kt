package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadTerm

class FieldRead(
    override val outerScope: StaticScope,
    override val term: FieldReadTerm,
    val subject: Expression,
) : Expression() {
    data class InvalidSubjectTypeError(
        override val location: SourceLocation?,
        val invalidSubjectType: MembershipType,
    ) : SemanticError

    sealed interface InferredFieldTypeOutcome

    data class InferredFieldTypeResult(
        val fieldType: MembershipType,
    ) : InferredFieldTypeOutcome

    data class MissingFieldError(
        override val location: SourceLocation?,
        val subjectType: UnorderedTupleType,
        val missingFieldName: Symbol,
    ) : InferredFieldTypeOutcome, SemanticError

    data object InferredFieldTypeAbort : InferredFieldTypeOutcome

    companion object {
        fun build(
            context: BuildContext,
            term: FieldReadTerm,
        ): FieldRead = FieldRead(
            outerScope = context.outerScope,
            term = term,
            subject = Expression.build(
                context = context,
                term = term.subject,
            ),
        )
    }

    private val fieldName: Symbol
        get() = term.fieldName

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectAnalysis = compute(subject.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val inferredSubjectType = subjectAnalysis.inferredType
        val validSubjectType = inferredSubjectType as? UnorderedTupleType

        if (validSubjectType != null) {
            val fieldType = validSubjectType.getFieldType(key = fieldName)

            if (fieldType != null) {
                DiagnosedAnalysis(
                    analysis = Analysis(
                        inferredType = fieldType,
                    ),
                    directErrors = emptySet(),
                )
            } else {
                DiagnosedAnalysis.fromError(
                    MissingFieldError(
                        location = term.location,
                        subjectType = validSubjectType,
                        missingFieldName = fieldName,
                    )
                )
            }
        } else {
            DiagnosedAnalysis.fromError(
                InvalidSubjectTypeError(
                    location = subject.location,
                    invalidSubjectType = inferredSubjectType,
                )
            )
        }
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        subject.classifiedValue.transformThen {
            if (it !is DictValue) throw IllegalStateException("Subject $it is not a dict")

            it.apply(
                argument = fieldName,
            )
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
