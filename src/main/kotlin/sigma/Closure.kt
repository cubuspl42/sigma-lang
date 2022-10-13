package sigma

class Closure(
    private val context: Table,
    private val argumentName: Symbol,
    private val image: Expression,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Value = image.evaluate(
        context = ChainedTable(
            context = context,
            table = ArgumentTable(
                name = argumentName,
                value = argument,
            ),
        ),
    )

    override fun dump(): String = "(closure)"
}
