package sigma.values

import sigma.Thunk
import sigma.expressions.Expression
import sigma.expressions.TupleTypeLiteral
import sigma.values.tables.Scope
import sigma.values.tables.Table
import sigma.values.tables.chainWith

class Closure(
    private val context: Scope,
    private val argumentType: TupleTypeLiteral,
    private val image: Expression,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Thunk = image.evaluate(
        scope = argumentType.toArgumentScope(
            argument = argument as Table,
        ).chainWith(
            context = context,
        ),
    )

    override fun dump(): String = "(closure)"
}
