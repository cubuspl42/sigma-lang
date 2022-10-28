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
    val determinedType: TypeExpression = object : TypeExpression {
        override fun evaluate(context: StaticScope): Type? {
            val declaredType = valueType ?: return value.inferType(scope = context)

            return declaredType.evaluate(context = context)
        }
    }

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
