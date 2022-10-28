package sigma.values

import sigma.StaticScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.TypeExpression
import sigma.expressions.Declaration
import sigma.expressions.Expression
import sigma.types.Type
import sigma.values.Symbol

class LoopedStaticValueScope(
    private val context: StaticScope,
    private val declarations: Map<Symbol, TypeExpression>,
) : StaticValueScope {
    override fun getValueType(
        valueName: Symbol,
    ): Type? = declarations[valueName]?.evaluate(
        context = context.copy(valueScope = this),
    ) ?: context.getValueType(
        valueName = valueName,
    )
}
