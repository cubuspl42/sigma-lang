package sigma.expressions

import sigma.StaticScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError
import sigma.values.tables.Scope

data class Reference(
    val referee: Symbol,
) : Expression() {
    companion object {
        fun build(
            reference: ReferenceContext,
        ): Reference = Reference(
            referee = Symbol(name = reference.referee.text),
        )
    }

    override fun inferType(
        scope: StaticScope,
    ): Type = scope.getValueType(
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
