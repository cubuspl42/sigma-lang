package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.DictAssociationContext
import sigma.parser.antlr.SigmaParser.DictConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.Scope

data class DictConstructorTerm(
    override val location: SourceLocation,
    val associations: List<Association>,
) : ExpressionTerm() {
    data class Association(
        val key: ExpressionTerm,
        val value: ExpressionTerm,
    ) {
        companion object {
            fun build(ctx: DictAssociationContext): Association {
                return Association(
                    key = ExpressionTerm.build(ctx.key),
                    value = ExpressionTerm.build(ctx.value),
                )
            }
        }
    }

    companion object {
        fun build(
            ctx: DictConstructorContext,
        ): DictConstructorTerm = DictConstructorTerm(
            location = SourceLocation.build(ctx),
            associations = ctx.dictAssociation().map {
                Association.build(it)
            },
        )
    }

    override fun dump(): String = "(dict literal)"
}
