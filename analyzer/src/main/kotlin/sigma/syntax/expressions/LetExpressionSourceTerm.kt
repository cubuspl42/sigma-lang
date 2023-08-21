package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.syntax.SourceLocation

data class LetExpressionSourceTerm(
    override val location: SourceLocation,
    val localScope: LocalScopeSourceTerm,
    val result: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpressionSourceTerm = LetExpressionSourceTerm(
            location = SourceLocation.build(ctx),
            localScope = LocalScopeSourceTerm.build(ctx.scope),
            result = ExpressionSourceTerm.build(ctx.result),
        )
    }

    override fun dump(): String = "(let expression)"
}
