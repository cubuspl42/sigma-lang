package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol

interface Scope {
    object Empty : Scope {
        override fun getValue(name: Symbol): EvaluationResult? = null
    }

    fun getValue(name: Symbol): EvaluationResult?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    context = context,
    scope = this,
)
