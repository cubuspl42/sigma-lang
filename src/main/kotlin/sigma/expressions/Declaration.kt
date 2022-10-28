package sigma.expressions

import sigma.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext

data class Declaration(
    val name: Symbol,
    val valueType: TypeExpression? = null,
    val value: Expression,
) {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): Declaration = Declaration(
            name = Symbol.of(ctx.name.text),
            valueType = ctx.valueType?.let { TypeExpression.build(it) },
            value = Expression.build(ctx.value),
        )
    }
}
