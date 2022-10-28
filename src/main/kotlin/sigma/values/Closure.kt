package sigma.values

import sigma.ArgumentTable
import sigma.Thunk
import sigma.expressions.Expression
import sigma.values.tables.Scope

class Closure(
    private val context: Scope,
    private val argumentName: Symbol,
    private val image: Expression,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Thunk = image.evaluate(
        scope = ArgumentTable(
            name = argumentName,
            value = argument,
        ).chainWith(
            context = context,
        ),
    )

    override fun dump(): String = "(closure)"
}
