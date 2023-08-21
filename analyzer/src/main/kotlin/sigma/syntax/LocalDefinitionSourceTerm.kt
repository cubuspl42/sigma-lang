package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.DefinitionContext
import sigma.syntax.expressions.ExpressionSourceTerm

data class LocalDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionSourceTerm? = null,
    override val body: ExpressionSourceTerm,
) : SourceTerm(), LocalDefinitionTerm {
    companion object {
        fun build(
            ctx: DefinitionContext,
        ): LocalDefinitionSourceTerm = LocalDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.valueType?.let { ExpressionSourceTerm.build(it) },
            body = ExpressionSourceTerm.build(ctx.value),
        )
    }
}
