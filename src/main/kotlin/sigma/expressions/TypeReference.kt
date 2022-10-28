package sigma.expressions

import sigma.Symbol
import sigma.Type
import sigma.TypeContext

data class TypeReference(
    val referee: Symbol,
) : TypeExpression {
    override fun evaluate(
        context: TypeContext,
    ): Type? = context.getType(
        name = referee,
    )
}