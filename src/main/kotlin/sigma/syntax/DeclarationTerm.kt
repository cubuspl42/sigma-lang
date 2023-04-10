package sigma.syntax

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.semantics.types.Type
import sigma.syntax.expressions.ExpressionTerm

data class DeclarationTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val valueType: TypeExpressionTerm? = null,
    val value: ExpressionTerm,
): Term() {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): DeclarationTerm = DeclarationTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            valueType = ctx.valueType?.let { TypeExpressionTerm.build(it) },
            value = ExpressionTerm.build(ctx.value),
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
