package sigma.evaluation.values

import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.semantics.expressions.Expression
import sigma.semantics.types.TupleType

class Closure(
    private val context: Scope,
    private val argumentType: TupleType,
    private val image: Expression,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Value = image.evaluate(
        scope = argumentType.toArgumentScope(
            argument = argument as DictValue,
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
