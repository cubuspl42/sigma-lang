package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ErrorExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface ReferenceTerm : ExpressionTerm {
    data class BuildOutput(
        val expressionLazy: Lazy<Expression>,
        val errorsLazy: Lazy<Set<SemanticError>>,
    ) {
        val expression: Expression by expressionLazy
        val errors: Set<SemanticError> by errorsLazy
    }

    data class UnresolvedNameError(
        override val location: SourceLocation?,
        val name: Symbol,
    ) : SemanticError

    companion object {
        fun build(
            context: Expression.BuildContext,
            term: ExpressionTerm,
            referredName: Symbol,
        ): BuildOutput {
            val outerScope = context.outerScope

            val resolvedName: LeveledResolvedIntroduction? by lazy {
                outerScope.resolveNameLeveled(name = referredName)
            }

            val expressionLazy = lazy {
                resolvedName?.resolvedIntroduction?.buildReference() ?: ErrorExpression(
                    term = term,
                )
            }

            val expression by expressionLazy

            val errorsLazy = lazy {
                if (resolvedName == null) {
                    setOf(
                        UnresolvedNameError(
                            location = null,
                            name = referredName,
                        ),
                    )
                } else {
                    expression.errors
                }
            }

            return BuildOutput(
                expressionLazy = expressionLazy,
                errorsLazy = errorsLazy,
            )
        }
    }

    val referredName: Identifier
}

fun ReferenceTerm.build(
    context: Expression.BuildContext,
): ReferenceTerm.BuildOutput = ReferenceTerm.build(
    context = context,
    term = this,
    referredName = referredName,
)
