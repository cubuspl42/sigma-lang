package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.syntax.LocalDefinitionTerm
import sigma.syntax.SourceLocation

data class LetExpressionSourceTerm(
    override val location: SourceLocation,
    val localScope: LocalScopeSourceTerm,
    override val result: ExpressionSourceTerm,
) : ExpressionSourceTerm(), LetExpressionTerm {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpressionSourceTerm = LetExpressionSourceTerm(
            location = SourceLocation.build(ctx),
            localScope = LocalScopeSourceTerm.build(ctx.scope),
            result = ExpressionSourceTerm.build(ctx.result),
        )
    }

    override val definitions: List<LocalDefinitionTerm>
        get() = localScope.definitions

    override fun dump(): String = "(let expression)"
}
