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

    override fun apply(scope: Scope, key: Value): Value {
        TODO("Not yet implemented")
    }

    override fun dump(): String = "\"$text\""
}
