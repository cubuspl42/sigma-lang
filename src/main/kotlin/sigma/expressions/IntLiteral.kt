package sigma.expressions

import sigma.StaticScope
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.types.IntType
import sigma.types.Type
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.Scope

data class IntLiteral(
    val value: Int,
) : Expression() {
    companion object {
        fun of(value: Int) = IntLiteral(
            value = value,
        )

        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteral = IntLiteral(
            value = ctx.text.toInt(),
        )
    }

    override fun inferType(scope: StaticScope): Type = IntType

    override fun evaluate(
        scope: Scope,
    ): Value = IntValue(
        value = value,
    )

    override fun dump(): String = value.toString()
}
