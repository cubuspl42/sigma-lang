package sigma.syntax

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.semantics.types.Type
import sigma.syntax.expressions.ExpressionTerm

data class DefinitionTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val valueType: TypeExpressionTerm? = null,
    val value: ExpressionTerm,
): Term() {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): DefinitionTerm = DefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            valueType = ctx.valueType?.let { TypeExpressionTerm.build(it) },
            value = ExpressionTerm.build(ctx.value),
        )
    }

    override fun validate(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) {
        value.validate(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        // TODO: Check if inferred type matches the declared one
    }

    fun determineAssumedType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type = determineDeclaredType(
        typeScope = typeScope,
    ) ?: inferType(
        typeScope = typeScope,
        valueScope = valueScope,
    )

    private fun determineDeclaredType(
        typeScope: TypeScope,
    ): Type? = valueType?.evaluate(
        typeScope = typeScope,
    )

    fun inferType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type = value.determineType(
        typeScope = typeScope,
        valueScope = valueScope,
    )
}
