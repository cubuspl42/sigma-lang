package sigma.expressions

import sigma.Symbol
import sigma.Table
import sigma.Value
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
    ): Value = context.apply(referee)

    override fun dump(): String = referee.dump()
}
