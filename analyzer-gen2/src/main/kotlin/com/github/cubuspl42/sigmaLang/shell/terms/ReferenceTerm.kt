package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.Scope

data class ReferenceTerm(
    val referredName: IdentifierTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.ReferenceContext,
        ): ReferenceTerm = ReferenceTerm(
            referredName = IdentifierTerm.build(ctx.referredName),
        )
    }

    override fun construct(context: ConstructionContext): Lazy<Expression> {
        val scope = context.scope

        return when (val resolution = scope.resolveName(referredName = referredName)) {
            is Scope.ArgumentReference -> lazyOf(
                Reference(
                    referredAbstractionLazy = resolution.referredAbstractionLazy,
                ),
            )

            is Scope.DefinitionReference -> resolution.referredBodyLazy

            Scope.UnresolvedReference -> throw IllegalStateException("Unresolved reference: $referredName")
        }
    }
}
