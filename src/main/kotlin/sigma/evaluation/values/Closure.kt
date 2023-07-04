package sigma.evaluation.values

import sigma.evaluation.Thunk
import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.tables.Table
import sigma.semantics.expressions.Expression
import sigma.semantics.types.TupleType

class Closure(
    private val context: Scope,
    private val argumentType: TupleType,
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

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
