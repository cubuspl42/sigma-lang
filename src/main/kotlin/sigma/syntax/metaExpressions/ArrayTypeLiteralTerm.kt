package sigma.syntax.metaExpressions

import sigma.TypeScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.ArrayType

data class ArrayTypeLiteralTerm(
    override val location: SourceLocation,
    val elementType: MetaExpressionTerm,
) : MetaExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.ArrayTypeLiteralContext,
        ): ArrayTypeLiteralTerm = ArrayTypeLiteralTerm(
            location = SourceLocation.build(ctx),
            elementType = build(ctx.type),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): ArrayType = ArrayType(
        elementType = elementType.evaluate(typeScope = typeScope),
    )
}
