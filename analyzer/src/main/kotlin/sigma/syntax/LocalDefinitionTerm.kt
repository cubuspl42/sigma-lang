package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.DefinitionContext
import sigma.syntax.expressions.ExpressionTerm

data class LocalDefinitionTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionTerm? = null,
    override val body: ExpressionTerm,
) : Term(), DefinitionTerm {
    companion object {
        fun build(
            ctx: DefinitionContext,
        ): LocalDefinitionTerm = LocalDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.valueType?.let { ExpressionTerm.build(it) },
            body = ExpressionTerm.build(ctx.value),
        )
    }
}
