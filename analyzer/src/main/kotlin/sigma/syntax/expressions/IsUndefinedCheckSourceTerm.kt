package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.IsUndefinedCheckContext
import sigma.syntax.SourceLocation

data class IsUndefinedCheckSourceTerm(
    override val location: SourceLocation,
    val argument: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: IsUndefinedCheckContext,
        ): IsUndefinedCheckSourceTerm = IsUndefinedCheckSourceTerm(
            location = SourceLocation.build(ctx),
            argument = ExpressionSourceTerm.build(ctx),
        )
    }

    override fun dump(): String = "(isUndefined)"
}
