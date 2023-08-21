package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.IfExpressionContext
import sigma.syntax.SourceLocation

data class IfExpressionSourceTerm(
    override val location: SourceLocation,
    val guard: ExpressionSourceTerm,
    val trueBranch: ExpressionSourceTerm,
    val falseBranch: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: IfExpressionContext,
        ): IfExpressionSourceTerm = IfExpressionSourceTerm(
            location = SourceLocation.build(ctx),
            guard = ExpressionSourceTerm.build(ctx.guard),
            trueBranch = ExpressionSourceTerm.build(ctx.trueBranch),
            falseBranch = ExpressionSourceTerm.build(ctx.falseBranch),
        )
    }

    override fun dump(): String = """
        if (${guard.dump()}) (
            %then ${trueBranch.dump()},
            %else ${falseBranch.dump()},
        )
    """.trimIndent()
}
