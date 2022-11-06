package sigma

import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class TypeReference(
    val referee: Symbol,
) : TypeExpression {
    override fun evaluate(
        context: StaticScope,
    ): Type = context.getType(
        typeName = referee,
    ) ?: throw TypeError(message ="Unresolved type ${referee.dump()}")
}
