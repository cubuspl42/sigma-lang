package sigma.syntax.typeExpressions

import sigma.TypeScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.semantics.types.DictType
import sigma.semantics.types.PrimitiveType
import sigma.values.TypeErrorException

data class DictTypeTerm(
    override val location: SourceLocation,
    val keyType: TypeExpressionTerm,
    val valueType: TypeExpressionTerm,
) : TypeExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.DictTypeDepictionContext,
        ): DictTypeTerm = DictTypeTerm(
            location = SourceLocation.build(ctx),
            keyType = build(ctx.keyType),
            valueType = build(ctx.valueType),
        )
    }

    override fun evaluate(
        typeScope: TypeScope,
    ): DictType = DictType(
        keyType = keyType.evaluate(
            typeScope = typeScope,
        ) as? PrimitiveType ?: throw TypeErrorException(
            location = keyType.location,
            message = "Dict key type is not primitive",
        ),
        valueType = valueType.evaluate(
            typeScope = typeScope,
        ),
    )
}
