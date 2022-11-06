package sigma

import sigma.expressions.SourceLocation
import sigma.types.Type
import sigma.values.Symbol
import sigma.values.TypeError

data class TypeReference(
    // TODO
    override val location: SourceLocation = SourceLocation.Invalid,
    val referee: Symbol,
) : TypeExpression() {
    override fun evaluate(
        context: StaticScope,
    ): Type = context.getType(
        typeName = referee,
    ) ?: throw TypeError(message = "Unresolved type ${referee.dump()}")
}
