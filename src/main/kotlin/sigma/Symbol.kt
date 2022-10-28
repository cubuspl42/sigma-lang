package sigma

import sigma.parser.antlr.SigmaParser.IdentifierContext

data class Symbol(
    val name: String,
) : PrimitiveValue() {
    companion object {
        fun of(
            name: String,
        ): Symbol = Symbol(
            name = name,
        )

        fun build(
            ctx: IdentifierContext,
        ): Symbol {
            return Symbol(name = ctx.text)
        }
    }

    override fun dump(): String = "`$name`"

    override fun isSame(other: Value): Boolean {
        if (other !is Symbol) return false
        return name == other.name
    }
}
