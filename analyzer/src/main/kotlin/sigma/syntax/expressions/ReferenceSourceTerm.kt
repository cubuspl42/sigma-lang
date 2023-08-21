package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

data class ReferenceSourceTerm(
    override val location: SourceLocation,
    override val referredName: Symbol,
) : ExpressionSourceTerm(), ReferenceTerm {
    companion object {
        fun build(
            ctx: ReferenceContext,
        ): ReferenceSourceTerm = ReferenceSourceTerm(
            location = SourceLocation.build(ctx),
            referredName = Symbol(name = ctx.referee.text),
        )
    }

    override fun dump(): String = referredName.dump()
}
