package sigma.expressions

import sigma.StaticScope
import sigma.TypeExpression
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.types.Type

data class Declaration(
    val name: Symbol,
    val valueType: TypeExpression? = null,
    val value: Expression,
) {
    fun determineType(
        context: StaticScope,
    ): Type = valueType?.evaluate(
        context = context,
    ) ?: value.inferType(
        scope = context,
    )

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
