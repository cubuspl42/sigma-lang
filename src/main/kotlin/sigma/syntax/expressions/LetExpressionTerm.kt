package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.syntax.SourceLocation
import sigma.evaluation.scope.Scope

data class LetExpressionTerm(
    override val location: SourceLocation,
    val localScope: LocalScopeTerm,
    val result: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpressionTerm = LetExpressionTerm(
            location = SourceLocation.build(ctx),
            localScope = LocalScopeTerm.build(ctx.scope),
            result = ExpressionTerm.build(ctx.result),
        )
    }

    override fun dump(): String = "(let expression)"
}
