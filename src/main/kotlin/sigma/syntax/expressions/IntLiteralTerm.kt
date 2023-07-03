package sigma.syntax.expressions


import sigma.parser.antlr.SigmaParser.IntLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Value
import sigma.evaluation.scope.Scope

data class IntLiteralTerm(
    override val location: SourceLocation,
    val value: IntValue,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: IntLiteralAltContext,
        ): IntLiteralTerm = IntLiteralTerm(
            location = SourceLocation.build(ctx),
            value = IntValue(value = ctx.text.toLong()),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = value

    override fun dump(): String = value.toString()
}
