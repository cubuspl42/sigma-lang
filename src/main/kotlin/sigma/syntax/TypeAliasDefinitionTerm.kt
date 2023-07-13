package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.TypeAliasDefinitionContext
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class TypeAliasDefinitionTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val definer: TypeExpressionTerm,
) : StaticStatementTerm() {
    companion object {
        fun build(
            ctx: TypeAliasDefinitionContext,
        ): TypeAliasDefinitionTerm = TypeAliasDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            definer = ctx.definer.let { TypeExpressionTerm.build(it) },
        )
    }
}
