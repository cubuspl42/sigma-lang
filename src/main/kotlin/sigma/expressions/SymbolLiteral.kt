package sigma.expressions

import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser
import sigma.values.tables.Scope

data class SymbolLiteral(
    val symbol: Symbol,
) : Expression {
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

    override fun evaluate(
        context: Scope,
    ): Value = symbol

    override fun dump(): String = symbol.dump()
}
