package sigma

import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TypeExpression
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class TypeReference(
    override val location: SourceLocation,
    val referee: Symbol,
) : TypeExpression() {
    override fun evaluate(
        typeScope: StaticTypeScope,
    ): Type = typeScope.getType(
        typeName = referee,
    ) ?: throw TypeError(message = "Unresolved type ${referee.dump()}")
}
