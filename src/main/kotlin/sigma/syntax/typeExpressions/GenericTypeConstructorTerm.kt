package sigma.syntax.typeExpressions

import sigma.parser.antlr.SigmaParser
import sigma.semantics.DeclarationScope
import sigma.semantics.types.GenericTypeConstructor
import sigma.semantics.types.TypeEntity
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.GenericParametersTuple

data class GenericTypeConstructorTerm(
    override val location: SourceLocation,
    val genericParametersTuple: GenericParametersTuple,
    val body: TypeExpressionTerm,
) : TypeExpressionTerm() {
    override fun evaluate(
        declarationScope: DeclarationScope,
    ): TypeEntity = GenericTypeConstructor(
        body = body,
    )

    companion object {
        fun build(
            ctx: SigmaParser.GenericTypeConstructorContext,
        ): TypeExpressionTerm = GenericTypeConstructorTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = GenericParametersTuple.build(ctx.genericParametersTuple()),
            body = TypeExpressionTerm.build(ctx.body),
        )
    }
}
