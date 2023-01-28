package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.types.ArrayType

data class ArrayTypeLiteral(
    override val location: SourceLocation,
    val elementType: TypeExpression,
) : TypeExpression() {
    companion object {
        fun build(
            ctx: SigmaParser.ArrayTypeLiteralContext,
        ): ArrayTypeLiteral = ArrayTypeLiteral(
            location = SourceLocation.build(ctx),
            elementType = build(ctx.type),
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): ArrayType = ArrayType(
        elementType = elementType.evaluate(typeScope = typeScope),
    )
}
