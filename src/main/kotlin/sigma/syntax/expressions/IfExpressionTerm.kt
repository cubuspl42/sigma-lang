package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.IfExpressionContext
import sigma.syntax.SourceLocation

data class IfExpressionTerm(
    override val location: SourceLocation,
    val guard: ExpressionTerm,
    val trueBranch: ExpressionTerm,
    val falseBranch: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: IfExpressionContext,
        ): IfExpressionTerm = IfExpressionTerm(
            location = SourceLocation.build(ctx),
            guard = ExpressionTerm.build(ctx.guard),
            trueBranch = ExpressionTerm.build(ctx.trueBranch),
            falseBranch = ExpressionTerm.build(ctx.falseBranch),
        )
    }

    override fun dump(): String = """
        if (${guard.dump()}) (
            %then ${trueBranch.dump()},
            %else ${falseBranch.dump()},
        )
    """.trimIndent()
}
