package sigma.evaluation.scope

import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.EvaluationContext

interface Scope {
    object Empty : Scope {
        override fun getValue(context: EvaluationContext, name: Symbol): EvaluationResult? = null
    }

    fun getValue(
        context: EvaluationContext,
        name: Symbol,
    ): EvaluationResult?
}

fun Scope.chainWith(
    context: Scope,
): Scope = ChainedScope(
    outerScope = context,
    scope = this,
)
