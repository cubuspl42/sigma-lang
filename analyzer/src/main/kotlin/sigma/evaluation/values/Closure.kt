package sigma.evaluation.values

import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.semantics.expressions.Expression
import sigma.semantics.types.TupleType

class Closure(
    private val outerScope: Scope,
    private val argumentType: TupleType,
    private val image: Expression,
) : ComputableFunctionValue() {
    override fun apply(
        argument: Value,
    ): Thunk<Value> = image.bind(
        scope = argumentType.toArgumentScope(
            argument = argument as DictValue,
        ).chainWith(
            context = outerScope,
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