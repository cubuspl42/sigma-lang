package sigma.values.tables

import sigma.Thunk
import sigma.values.Symbol

interface Scope {
    fun get(name: Symbol): Thunk?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    context = context,
    scope = this,
)
