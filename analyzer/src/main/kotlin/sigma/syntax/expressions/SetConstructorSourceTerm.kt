package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.SetConstructorContext
import sigma.syntax.SourceLocation

data class SetConstructorSourceTerm(
    override val location: SourceLocation,
    val elements: List<ExpressionSourceTerm>,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: SetConstructorContext,
        ): SetConstructorSourceTerm = SetConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            elements = ctx.elements.map {
                ExpressionSourceTerm.build(it)
            },
        )
    }

    override fun dump(): String = "(set constructor)"
}
