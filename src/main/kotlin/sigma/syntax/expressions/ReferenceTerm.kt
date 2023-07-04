package sigma.syntax.expressions

import sigma.evaluation.Thunk
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.evaluation.scope.Scope

data class ReferenceTerm(
    override val location: SourceLocation,
    val referee: Symbol,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: ReferenceContext,
        ): ReferenceTerm = ReferenceTerm(
            location = SourceLocation.build(ctx),
            referee = Symbol(name = ctx.referee.text),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk = scope.getValue(referee) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referee",
    )

    override fun dump(): String = referee.dump()
}
