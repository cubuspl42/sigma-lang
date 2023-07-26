package sigma.syntax.typeExpressions

import sigma.semantics.StaticScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.ArrayType
import sigma.semantics.types.TypeEntity

data class ArrayTypeConstructorTerm(
    override val location: SourceLocation,
    val elementType: TypeExpressionTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ArrayTypeConstructorContext,
        ): ArrayTypeConstructorTerm = ArrayTypeConstructorTerm(
            location = SourceLocation.build(ctx),
            elementType = build(ctx.type),
        )
    }

    override fun evaluate(
        declarationScope: StaticScope,
    ): TypeEntity = ArrayType(
        elementType = elementType.evaluateAsType(declarationScope = declarationScope),
    )
}
