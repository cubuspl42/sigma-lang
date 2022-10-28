package sigma.expressions

import sigma.Thunk
import sigma.values.Symbol
import sigma.values.tables.Table
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.ReferenceContext

data class Reference(
    val referee: Symbol,
) : Expression {
    companion object {
        fun build(
            reference: ReferenceContext,
        ): Reference = Reference(
            referee = Symbol(name = reference.referee.text),
        )
    }

    override fun evaluate(
        context: Table,
    ): Thunk = context.apply(referee)

    override fun dump(): String = referee.dump()
}
