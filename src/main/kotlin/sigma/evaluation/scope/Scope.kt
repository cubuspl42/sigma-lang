package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.semantics.expressions.EvaluationContext

interface Scope {
    object Empty : Scope {
        override fun getValue(name: Symbol): Thunk<*>? = null
    }

    fun getValue(
        name: Symbol,
    ): Thunk<*>?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    outerScope = context,
    scope = this,
)
