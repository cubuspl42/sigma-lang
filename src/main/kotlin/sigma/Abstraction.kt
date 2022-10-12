package sigma

import sigma.parser.antlr.SigmaParser.AbstractionContext

data class Abstraction(
    val argument: String,
    val image: Expression,
) : Expression {
    companion object {
        fun build(
            abstraction: AbstractionContext,
        ): Abstraction = Abstraction(
            argument = abstraction.argument.text,
            image = Expression.build(abstraction.image),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = Closure(
        scope = scope,
        argument = argument,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

data class Closure(
    val scope: Scope,
    val argument: String,
    val image: Expression,
) : FunctionValue() {
    companion object {
        fun build(
            symbol: AbstractionContext,
        ): Abstraction = Abstraction(
            argument = symbol.argument.text,
            image = Expression.build(symbol.image),
        )
    }

    override fun apply(
        argument: Value,
    ): Value = image.evaluate(
        scope = scope.extend(
            label = this.argument,
            value = argument,
        ),
    )

    override fun dump(): String = "(closure)"
}
