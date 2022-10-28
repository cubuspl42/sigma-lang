package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.types.SymbolType
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.Scope

data class SymbolLiteral(
    val symbol: Symbol,
) : Expression() {
    companion object {
        fun of(name: String) = SymbolLiteral(
            symbol = Symbol.of(name),
        )

        fun build(
            ctx: SigmaParser.IdentifierContext,
        ): SymbolLiteral = SymbolLiteral(
            symbol = Symbol(name = ctx.text),
        )
    }

    override fun inferType(): Type = SymbolType

    override fun evaluate(
        scope: Scope,
    ): Value = symbol

    override fun dump(): String = symbol.dump()
}
