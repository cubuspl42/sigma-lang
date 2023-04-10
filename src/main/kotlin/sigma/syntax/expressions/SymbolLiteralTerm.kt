package sigma.syntax.expressions


import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.SymbolType
import sigma.semantics.types.Type
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.Scope

data class SymbolLiteralTerm(
    override val location: SourceLocation,
    val symbol: Symbol,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: SymbolLiteralAltContext,
        ): SymbolLiteralTerm = SymbolLiteralTerm(
            location = SourceLocation.build(ctx),
            symbol = Symbol(
                name = ctx.text.drop(1).dropLast(1),
            ),
        )
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = SymbolType(
        value = symbol,
    )

    override fun evaluate(
        scope: Scope,
    ): Value = symbol

    override fun dump(): String = symbol.dump()
}
