package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser

data class ArrayTypeConstructorSourceTerm(
    override val location: SourceLocation,
    val elementType: ExpressionSourceTerm,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ArrayTypeConstructorContext,
        ): ArrayTypeConstructorSourceTerm = ArrayTypeConstructorSourceTerm(
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
