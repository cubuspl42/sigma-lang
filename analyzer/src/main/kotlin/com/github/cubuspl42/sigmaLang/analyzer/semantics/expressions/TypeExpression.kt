package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class TypeExpression(
    private val context: Expression.BuildContext,
    private val bodyTerm: ExpressionTerm,
) {
    private data class DiagnosedAnalysis(
        val type: MembershipType?,
        val errors: Set<SemanticError>,
    )

    data class TypeEvaluationError(
        override val location: SourceLocation,
        val evaluationError: EvaluationError,
    ) : SemanticError

    data class NonTypeValueError(
        override val location: SourceLocation,
        val value: Value,
    ) : SemanticError

    companion object {
        fun build(
            outerMetaScope: StaticScope,
            term: ExpressionTerm,
        ): TypeExpression = TypeExpression(
            context = Expression.BuildContext(
                outerMetaScope = BuiltinScope,
                outerScope = outerMetaScope,
            ),
            bodyTerm = term,
        )
    }

    val body by lazy {
        Expression.build(
            context = context,
            term = bodyTerm,
        )
    }

    private val diagnosedAnalysis by lazy {
        val valueThunk by lazy {
            body.bind(
                dynamicScope = TranslationDynamicScope(
                    staticScope = context.outerScope,
                ),
            )
        }

        when (val outcome = valueThunk.outcome) {
            is EvaluationError -> DiagnosedAnalysis(
                type = null,
                errors = setOf(
                    TypeEvaluationError(
                        location = bodyTerm.location,
                        evaluationError = outcome,
                    ),
                ),
            )

            is EvaluationResult -> {
                val value = outcome.value

                when (val type = value.asType) {
                    null -> DiagnosedAnalysis(
                        type = null,
                        errors = setOf(
                            NonTypeValueError(
                                location = bodyTerm.location,
                                value = value,
                            ),
                        ),
                    )

                    else -> DiagnosedAnalysis(
                        type = type,
                        errors = emptySet(),
                    )
                }
            }
        }
    }

    val typeOrIllType: MembershipType by lazy {
        diagnosedAnalysis.type ?: IllType
    }

    val errors: Set<SemanticError> by lazy {
        diagnosedAnalysis.errors
    }
}
