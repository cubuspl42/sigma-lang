package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser

data class ArrayTypeConstructorTerm(
    override val location: SourceLocation,
    val elementType: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ArrayTypeConstructorContext,
        ): ArrayTypeConstructorTerm = ArrayTypeConstructorTerm(
            location = SourceLocation.build(ctx),
            elementType = build(ctx.type),
        )
    }

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): TypeEntity = ArrayType(
//        elementType = elementType.evaluateAsType(declarationScope = declarationScope),
//    )

    override fun dump(): String = "(array type constructor)"
}
