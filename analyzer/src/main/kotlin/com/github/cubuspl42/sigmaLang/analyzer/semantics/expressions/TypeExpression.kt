package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationError
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class TypeExpression(
    private val outerScope: StaticScope,
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
            outerScope: StaticScope,
            term: ExpressionTerm,
        ): TypeExpression = TypeExpression(
            outerScope = outerScope,
            bodyTerm = term,
        )
    }

    val body by lazy {
        Expression.build(
            outerScope = outerScope,
            term = bodyTerm,
        )
    }

    private val diagnosedAnalysis by lazy {
        // TODO: Switch to static analysis
        val valueThunk by lazy {
            body.bind(
                dynamicScope = TranslationDynamicScope(
                    staticScope = outerScope,
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
