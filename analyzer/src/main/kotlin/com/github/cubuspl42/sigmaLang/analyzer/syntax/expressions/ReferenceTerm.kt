package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ErrorExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface ReferenceTerm : ExpressionTerm {
    data class Analysis(
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
        fun analyze(
            context: Expression.BuildContext,
            term: ExpressionTerm,
            referredName: Symbol,
        ): Analysis {
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

            return Analysis(
                expressionLazy = expressionLazy,
                errorsLazy = errorsLazy,
            )
        }
    }

    val referredName: Identifier
}

fun ReferenceTerm.analyze(
    context: Expression.BuildContext,
): ReferenceTerm.Analysis = ReferenceTerm.analyze(
    context = context,
    term = this,
    referredName = referredName,
)
