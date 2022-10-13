package sigma

import sigma.parser.antlr.SigmaParser.AbstractionContext

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
        scope: Scope,
    ): Value = Closure(
        environment = scope,
        argumentName = argumentName,
        image = image,
    )

    override fun dump(): String = "(abstraction)"
}

data class Closure(
    val environment: Scope,
    val argumentName: Symbol,
    val image: Expression,
) : FunctionValue() {
//    companion object {
//        fun build(
//            symbol: AbstractionContext,
//        ): Abstraction = Abstraction(
//            argumentName = symbol.argument.text,
//            image = Expression.build(symbol.image),
//        )
//    }

    override fun apply(
        argument: Value,
    ): Value = image.evaluate(
        scope = LinkedScope(
            parent = environment,
            binds = mapOf(argumentName to argument),
        ),
    )

    override fun dump(): String = "(closure)"
}
