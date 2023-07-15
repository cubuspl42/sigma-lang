package sigma.syntax.typeExpressions

import sigma.semantics.TypeScope
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.TypeEntity

data class TypeReferenceTerm(
    override val location: SourceLocation,
    val referee: Symbol,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.TypeReferenceContext,
        ): TypeReferenceTerm = TypeReferenceTerm(
            location = SourceLocation.build(ctx),
            referee = Symbol.of(ctx.referee.text),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): TypeEntity {
        val typeDefinition = typeScope.getTypeDefinition(
            typeName = referee,
        ) ?: throw TypeErrorException(
            location = location,
            message = "Unresolved type ${referee.dump()}",
        )

        return typeDefinition.definedType
    }
}
