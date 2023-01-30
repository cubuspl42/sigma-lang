package sigma.syntax.typeExpressions

import sigma.StaticTypeScope
import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser
import sigma.types.ArrayType
import sigma.types.DictType
import sigma.types.PrimitiveType
import sigma.values.TypeError

data class DictTypeDepiction(
    override val location: SourceLocation,
    val keyType: TypeExpression,
    val valueType: TypeExpression,
) : TypeExpression() {
    companion object {
        fun build(
            ctx: SigmaParser.DictTypeDepictionContext,
        ): DictTypeDepiction = DictTypeDepiction(
            location = SourceLocation.build(ctx),
            keyType = build(ctx.keyType),
            valueType = build(ctx.valueType),
        )
    }

    override fun evaluate(
        typeScope: StaticTypeScope,
    ): DictType = DictType(
        keyType = keyType.evaluate(
            typeScope = typeScope,
        ) as? PrimitiveType ?: throw TypeError(
            location = keyType.location,
            message = "Dict key type is not primitive",
        ),
        valueType = valueType.evaluate(
            typeScope = typeScope,
        ),
    )
}
