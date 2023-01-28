package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.types.Type

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
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = determineDeclaredType(
        typeScope = typeScope,
    ) ?: inferType(
        typeScope = typeScope,
        valueScope = valueScope,
    )

    fun determineDeclaredType(
        typeScope: StaticTypeScope,
    ): Type? = valueType?.evaluate(
        typeScope = typeScope,
    )

    fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = value.inferType(
        typeScope = typeScope,
        valueScope = valueScope,
    )
}
