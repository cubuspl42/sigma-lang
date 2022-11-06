package sigma.expressions

import sigma.StaticScope
import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.types.SymbolType
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.Scope

data class SymbolLiteral(
    override val location: SourceLocation,
    val symbol: Symbol,
) : Expression() {
    companion object {
        fun of(name: String) = SymbolLiteral(
            location = SourceLocation.Invalid,
            symbol = Symbol.of(name),
        )

        fun build(
            ctx: SymbolLiteralAltContext,
        ): SymbolLiteral = SymbolLiteral(
            location = SourceLocation.build(ctx),
            symbol = Symbol(
                name = ctx.text.drop(1).dropLast(1),
            ),
        )
    }

    override fun inferType(
        scope: StaticScope,
    ): Type = SymbolType(
        value = symbol,
    )

    override fun evaluate(
        scope: Scope,
    ): Value = symbol

    override fun dump(): String = symbol.dump()
}
