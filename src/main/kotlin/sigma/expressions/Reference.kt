package sigma.expressions

import sigma.Thunk
import sigma.parser.antlr.SigmaParser.ReferenceContext
import sigma.types.Type
import sigma.values.Symbol
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

    override fun inferType(): Type {
        TODO("Not yet implemented")
    }

    override fun evaluate(
        context: Scope,
    ): Thunk = context.apply(referee)

    override fun dump(): String = referee.dump()
}
