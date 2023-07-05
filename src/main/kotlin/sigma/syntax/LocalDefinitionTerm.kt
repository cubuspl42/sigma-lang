package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class LocalDefinitionTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val type: TypeExpressionTerm? = null,
    override val definer: ExpressionTerm,
) : Term(), DefinitionTerm {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): LocalDefinitionTerm = LocalDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            type = ctx.valueType?.let { TypeExpressionTerm.build(it) },
            definer = ExpressionTerm.build(ctx.value),
        )
    }
}
