package sigma.expressions

import sigma.IntValue
import sigma.Symbol
import sigma.Table
import sigma.Value
import sigma.parser.antlr.SigmaParser

data class IntLiteral(
    val value: IntValue,
) : Expression {
    companion object {
        fun of(value: Int) = IntLiteral(
            value = IntValue(value)
        )

        fun build(
            ctx: SigmaParser.IdentifierContext,
        ): SymbolLiteral = SymbolLiteral(
            symbol = Symbol(name = ctx.text),
        )
    }

    override fun evaluate(
        context: Table,
    ): Value = value

    override fun dump(): String = value.dump()
}
