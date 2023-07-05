package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.ConstantDefinitionContext
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class ConstantDefinitionTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val type: TypeExpressionTerm? = null,
    override val definer: ExpressionTerm,
) : StaticStatementTerm(), DefinitionTerm {
    companion object {
        fun build(
            ctx: ConstantDefinitionContext,
        ): ConstantDefinitionTerm = ConstantDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            type = ctx.type?.let { TypeExpressionTerm.build(it) },
            definer = ExpressionTerm.build(ctx.definer),
        )
    }
}
