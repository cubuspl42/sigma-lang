package sigma

import sigma.parser.antlr.SigmaParser

data class Symbol(
    val text: String,
) : Value() {
    companion object {
        fun build(
            symbol: SigmaParser.SymbolContext,
        ): Symbol = Symbol(
            text = symbol.text.text
        )
    }

    override fun dump(): String = "\"$text\""
}
