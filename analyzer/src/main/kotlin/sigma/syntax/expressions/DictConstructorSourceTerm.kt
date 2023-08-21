package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.DictAssociationContext
import sigma.parser.antlr.SigmaParser.DictConstructorContext
import sigma.syntax.SourceLocation

data class DictConstructorSourceTerm(
    override val location: SourceLocation,
    val associations: List<Association>,
) : ExpressionSourceTerm() {
    data class Association(
        val key: ExpressionSourceTerm,
        val value: ExpressionSourceTerm,
    ) {
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
