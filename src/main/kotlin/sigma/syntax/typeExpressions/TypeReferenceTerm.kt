package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.parser.antlr.SigmaParser
import sigma.semantics.StaticScope
import sigma.semantics.TypeEntityDefinition
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
        declarationScope: StaticScope,
    ): TypeEntity {
        val resolvedDeclaration = declarationScope.resolveName(
            name = referee,
        ) ?: throw TypeErrorException(
            location = location,
            message = "Unresolved name ${referee.dump()}",
        )

        val typeDefinition = resolvedDeclaration as? TypeEntityDefinition ?: throw TypeErrorException(
            location = location,
            message = "Unresolved name ${referee.dump()}",
        )

        return typeDefinition.definedTypeEntity
    }
}
