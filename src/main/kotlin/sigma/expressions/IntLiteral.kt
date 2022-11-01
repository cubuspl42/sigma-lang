package sigma.expressions

import sigma.StaticScope
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.types.IntLiteralType
import sigma.types.Type
import sigma.values.IntValue
import sigma.values.Value
import sigma.values.tables.Scope

data class IntLiteral(
    val value: IntValue,
) : Expression() {
    companion object {
        fun of(value: Int) = IntLiteral(
            value = IntValue(value = value),
        )

        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteral = IntLiteral(
            value = IntValue(value = ctx.text.toInt()),
        )
    }

    override fun inferType(
        scope: StaticScope,
    ): Type = IntLiteralType(
        value = value,
    )

    override fun evaluate(
        scope: Scope,
    ): Value = value

    override fun dump(): String = value.toString()
}
