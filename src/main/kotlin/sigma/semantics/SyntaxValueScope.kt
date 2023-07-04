package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type

interface SyntaxValueScope {
    object Empty : SyntaxValueScope {
        override fun getValueType(
            valueName: Symbol,
        ): Type? = null
    }

    // Idea: Return `TypeExpression`?
    fun getValueType(valueName: Symbol): Type?
}
