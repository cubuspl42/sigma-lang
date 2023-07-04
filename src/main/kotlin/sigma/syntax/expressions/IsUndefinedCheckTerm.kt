package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.IsUndefinedCheckContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.UndefinedValue
import sigma.evaluation.scope.Scope

data class IsUndefinedCheckTerm(
    override val location: SourceLocation,
    val argument: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: IsUndefinedCheckContext,
        ): IsUndefinedCheckTerm = IsUndefinedCheckTerm(
            location = SourceLocation.build(ctx),
            argument = ExpressionTerm.build(ctx),
        )
    }

    override fun dump(): String = "(isUndefined)"
}
