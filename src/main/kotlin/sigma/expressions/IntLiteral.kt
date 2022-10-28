package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.types.IntType
import sigma.types.Type
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.Scope

data class IntLiteral(
    val value: IntValue,
) : Expression() {
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

    override fun inferType(): Type = IntType

    override fun evaluate(
        scope: Scope,
    ): Value = value

    override fun dump(): String = value.dump()
}
