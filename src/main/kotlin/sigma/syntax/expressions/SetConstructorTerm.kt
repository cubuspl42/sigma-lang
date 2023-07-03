package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.SetConstructorContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.SetType
import sigma.semantics.types.PrimitiveType
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.SetValue
import sigma.semantics.types.Type
import sigma.evaluation.values.TypeErrorException

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

    override fun determineType(typeScope: TypeScope, valueScope: SyntaxValueScope): Type {
        TODO("Not yet implemented")
    }

    override fun evaluate(
        scope: Scope,
    ): SetValue = SetValue(
        elements = elements.map {
            it.evaluate(scope = scope).toEvaluatedValue
        }.toSet(),
    )
}
