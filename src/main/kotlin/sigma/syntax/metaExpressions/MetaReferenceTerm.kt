package sigma.syntax.metaExpressions

import sigma.TypeScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.TypeErrorException
import sigma.parser.antlr.SigmaParser

data class MetaReferenceTerm(
    override val location: SourceLocation,
    val referee: Symbol,
) : MetaExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.TypeReferenceContext,
        ): MetaReferenceTerm = MetaReferenceTerm(
            location = SourceLocation.build(ctx),
            referee = Symbol.of(ctx.referee.text),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): Type = typeScope.getType(
        typeName = referee,
    ) ?: throw TypeErrorException(
        location = location,
        message = "Unresolved type ${referee.dump()}",
    )
}
