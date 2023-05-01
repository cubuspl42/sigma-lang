package sigma

import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.semantics.types.Type
import sigma.values.Symbol
import sigma.values.TypeErrorException

data class TypeReferenceTerm(
    override val location: SourceLocation,
    val referee: Symbol,
) : TypeExpressionTerm() {
    override fun evaluate(
        typeScope: TypeScope,
    ): Type = typeScope.getType(
        typeName = referee,
    ) ?: throw TypeErrorException(
        location = location,
        message = "Unresolved type ${referee.dump()}",
    )
}
