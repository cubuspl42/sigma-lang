package sigma.evaluation.scope

import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value

interface Scope {
    object Empty : Scope {
        override fun getValue(name: Symbol): Value? = null
    }

    fun getValue(name: Symbol): Value?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    context = context,
    scope = this,
)
