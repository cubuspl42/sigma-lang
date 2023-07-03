package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.SetConstructorContext
import sigma.syntax.SourceLocation
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.SetValue

data class SetConstructorTerm(
    override val location: SourceLocation,
    val elements: List<ExpressionTerm>,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: SetConstructorContext,
        ): SetConstructorTerm = SetConstructorTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.elements.map {
                ExpressionTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(set constructor)"

    override fun evaluate(
        scope: Scope,
    ): SetValue = SetValue(
        elements = elements.map {
            it.evaluate(scope = scope).toEvaluatedValue
        }.toSet(),
    )
}
