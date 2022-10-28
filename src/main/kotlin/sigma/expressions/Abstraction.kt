package sigma.expressions

import sigma.StaticScope
import sigma.values.Closure
import sigma.values.Symbol
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.types.FunctionType
import sigma.types.Type
import sigma.values.tables.Scope

data class Abstraction(
    val argumentName: Symbol,
    val image: Expression,
) : Expression() {
    companion object {
        fun build(
            abstraction: AbstractionContext,
        ): Abstraction = Abstraction(
            argumentName = Symbol(abstraction.argument.text),
            image = Expression.build(abstraction.image),
        )
    }

    override fun inferType(scope: StaticScope): Type = FunctionType(
        imageType = image.inferType(scope = scope),
    )

    override fun evaluate(
        scope: Scope,
    ): Value = Closure(
        context = scope,
        argumentName = argumentName,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}
