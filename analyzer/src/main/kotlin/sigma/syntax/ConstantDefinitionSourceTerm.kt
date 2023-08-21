package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.ConstantDefinitionContext
import sigma.syntax.expressions.ExpressionSourceTerm

data class ConstantDefinitionSourceTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionSourceTerm? = null,
    override val body: ExpressionSourceTerm,
) : NamespaceEntrySourceTerm(), DefinitionTerm {
    companion object {
        fun build(
            ctx: ConstantDefinitionContext,
        ): ConstantDefinitionSourceTerm = ConstantDefinitionSourceTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.type?.let { ExpressionSourceTerm.build(it) },
            body = ExpressionSourceTerm.build(ctx.definer),
        )
    }
}
