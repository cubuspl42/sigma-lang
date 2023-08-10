package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.ConstantDefinitionContext
import sigma.syntax.expressions.ExpressionTerm

data class ConstantDefinitionTerm(
    override val location: SourceLocation,
    override val name: Symbol,
    override val declaredTypeBody: ExpressionTerm? = null,
    override val body: ExpressionTerm,
) : NamespaceEntryTerm(), DefinitionTerm {
    companion object {
        fun build(
            ctx: ConstantDefinitionContext,
        ): ConstantDefinitionTerm = ConstantDefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            declaredTypeBody = ctx.type?.let { ExpressionTerm.build(it) },
            body = ExpressionTerm.build(ctx.definer),
        )
    }
}
