package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadTerm

abstract class FieldRead : Expression() {
    abstract override val term: FieldReadTerm

    abstract val subject: Expression

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
        val missingFieldName: Identifier,
    ) : InferredFieldTypeOutcome, SemanticError

    data object InferredFieldTypeAbort : InferredFieldTypeOutcome

    companion object {
        fun build(
            context: BuildContext,
            term: FieldReadTerm,
        ): Stub<FieldRead> = object : Stub<FieldRead> {
            override val resolved: FieldRead by lazy {
                object : FieldRead() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: FieldReadTerm = term

                    override val subject: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.subject,
                        ).resolved
                    }
                }
            }
        }
    }

    private val fieldName: Identifier
        get() = term.fieldName

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectAnalysis = compute(subject.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val inferredSubjectType = subjectAnalysis.inferredType as MembershipType
        val validSubjectType = inferredSubjectType as? UnorderedTupleType

        if (validSubjectType != null) {
            val fieldType = validSubjectType.getFieldType(key = fieldName)

            if (fieldType != null) {
                DiagnosedAnalysis(
                    analysis = Analysis(
                        inferredType = fieldType as MembershipType,
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

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = subject.bind(
        dynamicScope = dynamicScope,
    ).thenDo { subjectValue ->
        if (subjectValue !is DictValue) throw IllegalStateException("Subject $subjectValue is not a dict")

        subjectValue.apply(
            argument = fieldName,
        )
    }

    override val subExpressions: Set<Expression>
        get() = setOf(subject)
}
