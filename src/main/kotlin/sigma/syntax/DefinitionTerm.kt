package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaParser.DeclarationContext
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm

data class DefinitionTerm(
    override val location: SourceLocation,
    val name: Symbol,
    val valueType: TypeExpressionTerm? = null,
    val value: ExpressionTerm,
) : Term() {
    companion object {
        fun build(
            ctx: DeclarationContext,
        ): DefinitionTerm = DefinitionTerm(
            location = SourceLocation.build(ctx),
            name = Symbol.of(ctx.name.text),
            valueType = ctx.valueType?.let { TypeExpressionTerm.build(it) },
            value = ExpressionTerm.build(ctx.value),
        )
    }
}
