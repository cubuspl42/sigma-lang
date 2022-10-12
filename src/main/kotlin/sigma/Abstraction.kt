package sigma

import sigma.parser.antlr.SigmaParser.AbstractionContext

data class Abstraction(
    val argument: String,
    val image: Expression,
) : Value() {
    companion object {
        fun build(
            symbol: AbstractionContext,
        ): Abstraction = Abstraction(
            argument = symbol.argument.text,
            image = Expression.build(symbol.image),
        )
    }

    override fun apply(
        scope: Scope,
        key: Value,
    ): Value = image.evaluate(
        scope = scope.extend(
            label = argument,
            value = key,
        ),
    )

    override fun dump(): String = "(abstraction)"
}
