package sigma.evaluation.scope

import sigma.evaluation.Thunk
import sigma.syntax.expressions.ExpressionTerm
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Expression

class LoopedScope(
    private val context: Scope,
    private val expressionByName: Map<Symbol, Expression>,
) : Scope {
    private val thunkByName = mutableMapOf<Symbol, Thunk?>()

    override fun getValue(
        name: Symbol,
    ): Thunk? = thunkByName.getOrPut(name) {
        expressionByName[name]?.evaluate(
            scope = this,
        )
    } ?: context.getValue(
        name = name,
    )
}
