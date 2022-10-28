package sigma

import sigma.types.Type
import sigma.values.Symbol

data class TypeReference(
    val referee: Symbol,
) : TypeExpression {
    override fun evaluate(
        context: StaticScope,
    ): Type? = context.getType(
        typeName = referee,
    )
}
