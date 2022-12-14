package sigma.expressions


import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope

data class Reference(
    override val location: SourceLocation,
    val referee: Symbol,
) : Expression() {
    companion object {
        fun build(
            ctx: ReferenceContext,
        ): Reference = Reference(
            location = SourceLocation.build(ctx),
            referee = Symbol(name = ctx.referee.text),
        )
    }

    override fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = valueScope.getValueType(
        valueName = referee,
    ) ?: throw TypeError(
        message = "Unresolved reference: $referee"
    )

    override fun evaluate(
        scope: Scope,
    ): Thunk = scope.get(referee) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referee",
    )

    override fun dump(): String = referee.dump()
}
