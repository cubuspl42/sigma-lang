package sigma.expressions

import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.values.tables.Scope

data class Abstraction(
    val argumentName: Symbol,
    val image: Expression,
) : Expression {
    companion object {
        fun build(
            abstraction: AbstractionContext,
        ): Abstraction = Abstraction(
            argumentName = Symbol(abstraction.argument.text),
            image = Expression.build(abstraction.image),
        )
    }

    override fun evaluate(
        context: Scope,
    ): Value = Closure(
        context = context,
        argumentName = argumentName,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}
