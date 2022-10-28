package sigma.expressions

import sigma.StaticScope
import sigma.TypeExpression
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.types.Type
import sigma.values.TypeError

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

    fun determineAssumedType(
        scope: StaticScope,
    ): Type = determineDeclaredType(
        scope = scope,
    ) ?: inferType(
        scope = scope,
    )

    fun determineDeclaredType(
        scope: StaticScope,
    ): Type? = valueType?.evaluate(
        context = scope,
    )

    fun inferType(
        scope: StaticScope,
    ): Type = value.inferType(
        scope = scope,
    )
}
