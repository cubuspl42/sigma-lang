package sigma.syntax.expressions

import sigma.evaluation.values.IntValue
import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.syntax.SourceLocation

data class IntLiteralSourceTerm(
    override val location: SourceLocation,
    override val value: IntValue,
) : ExpressionSourceTerm(), IntLiteralTerm {
    companion object {
        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteralSourceTerm = IntLiteralSourceTerm(
            location = SourceLocation.build(ctx),
            value = IntValue(value = ctx.text.toLong()),
        )
    }

    override fun dump(): String = value.toString()
}
