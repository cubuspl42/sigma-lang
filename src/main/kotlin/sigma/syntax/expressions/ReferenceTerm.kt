package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
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

    override fun determineType(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ): Type = valueScope.getValueType(
        valueName = referee,
    ) ?: throw TypeErrorException(
        location = location,
        message = "Unresolved reference: $referee"
    )

    override fun evaluate(
        scope: Scope,
    ): Thunk = scope.getValue(referee) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referee",
    )

    override fun dump(): String = referee.dump()
}
