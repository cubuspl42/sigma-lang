package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.DictAssociationContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.DictConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class DictConstructorSourceTerm(
    override val location: SourceLocation,
    override val associations: List<Association>,
) : ExpressionSourceTerm(), DictConstructorTerm {
    data class Association(
        override val key: ExpressionTerm,
        override val value: ExpressionTerm,
    ): DictConstructorTerm.Association {
        companion object {
            fun build(ctx: DictAssociationContext): Association {
                return Association(
                    key = ExpressionSourceTerm.build(ctx.key),
                    value = ExpressionSourceTerm.build(ctx.value),
                )
            }
        }
    }

    companion object {
        fun build(
            ctx: DictConstructorContext,
        ): DictConstructorSourceTerm = DictConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            associations = ctx.dictAssociation().map {
                Association.build(it)
            },
        )
    }

    override fun dump(): String = "(dict literal)"
}
