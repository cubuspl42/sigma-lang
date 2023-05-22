package sigma.evaluation.scope

import sigma.Thunk
import sigma.evaluation.values.Symbol

interface Scope {
    object Empty : Scope {
        override fun get(name: Symbol): Thunk? = null
    }

    fun get(name: Symbol): Thunk?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    context = context,
    scope = this,
)
