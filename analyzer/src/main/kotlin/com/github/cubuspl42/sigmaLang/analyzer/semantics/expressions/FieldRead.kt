package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TableValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadTerm

abstract class FieldRead : FirstOrderExpression() {
    abstract override val term: FieldReadTerm?

    abstract val fieldName: Identifier

    abstract val subject: Expression

    data class InvalidSubjectTypeError(
        override val location: SourceLocation?,
        val invalidSubjectType: SpecificType,
    ) : SemanticError

    sealed interface InferredFieldTypeOutcome

    data class InferredFieldTypeResult(
        val fieldType: SpecificType,
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

                    override val fieldName: Identifier = term.fieldName

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

    override val computedAnalysis = buildAnalysisComputation {
        val subjectAnalysis = compute(subject.computedTypeInference) ?: return@buildAnalysisComputation null

        val inferredSubjectType = subjectAnalysis.inferredType as SpecificType
        val validSubjectType = inferredSubjectType as? UnorderedTupleType

        if (validSubjectType != null) {
            val fieldType = validSubjectType.getFieldType(key = fieldName)

            if (fieldType != null) {
                Analysis(
                    typeInference = TypeInference(
                        inferredType = fieldType,
                    ),
                    directErrors = emptySet(),
                )
            } else {
                Analysis.fromError(
                    MissingFieldError(
                        location = term?.location,
                        subjectType = validSubjectType,
                        missingFieldName = fieldName,
                    )
                )
            }
        } else {
            Analysis.fromError(
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
        if (subjectValue !is TableValue) throw IllegalStateException("Subject $subjectValue is not a table")

        subjectValue.apply(
            argument = fieldName,
        )
    }

    override val subExpressions: Set<Expression>
        get() = setOf(subject)
}

fun FieldRead(
    subjectLazy: Lazy<Expression>,
    fieldName: Identifier,
): FieldRead = object : FieldRead() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term = null

    override val subject: Expression by subjectLazy

    override val fieldName: Identifier = fieldName
}
