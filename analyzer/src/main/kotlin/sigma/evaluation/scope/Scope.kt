package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Thunk
import sigma.evaluation.values.Value

interface Scope {
    object Empty : Scope {
        override fun getValue(name: Symbol): Thunk<Value>? = null
    }

    fun getValue(
        name: Symbol,
    ): Thunk<Value>?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    outerScope = context,
    scope = this,
)
