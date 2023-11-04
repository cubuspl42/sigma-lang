package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub

interface ReferenceTerm : ExpressionTerm {
    companion object {
        fun build(
            context: Expression.BuildContext,
            referredName: Symbol,
        ): Stub<Expression> = object : Stub<Expression> {
            override val resolved: Expression by lazy {
                val outerScope = context.outerScope

                // TODO: Clean error
                val resolvedName = outerScope.resolveNameLeveled(name = referredName) ?: run {
                    throw IllegalStateException("Unresolved name at compile-time: $referredName")
                }

                resolvedName.resolvedIntroduction.buildReference()
            }
        }
    }

    val referredName: Identifier
}

fun ReferenceTerm.build(
    context: Expression.BuildContext,
): Stub<Expression> = ReferenceTerm.build(
    context = context,
    referredName = referredName,
)
