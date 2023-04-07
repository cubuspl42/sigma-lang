package sigma.syntax

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.semantics.types.Type
import sigma.syntax.expressions.Expression

data class Declaration(
    override val location: SourceLocation,
    val name: Symbol,
    val valueType: TypeExpression? = null,
    val value: Expression,
): Term() {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): Declaration = Declaration(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            valueType = ctx.valueType?.let { TypeExpression.build(it) },
            value = Expression.build(ctx.value),
        )
    }

    override fun validate(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
        value.validate(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        // TODO: Check if inferred type matches the declared one
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

    private fun determineDeclaredType(
        typeScope: StaticTypeScope,
    ): Type? = valueType?.evaluate(
        typeScope = typeScope,
    )

    fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = value.determineType(
        typeScope = typeScope,
        valueScope = valueScope,
    )
}
