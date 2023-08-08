package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.parser.antlr.SigmaParser

data class DictTypeConstructorTerm(
    override val location: SourceLocation,
    val keyType: ExpressionTerm,
    val valueType: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: SigmaParser.DictTypeDepictionContext,
        ): DictTypeConstructorTerm = DictTypeConstructorTerm(
            location = SourceLocation.build(ctx),
            keyType = build(ctx.keyType),
            valueType = build(ctx.valueType),
        )
    }

    override fun dump(): String = "(dict type constructor)"

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): TypeEntity = DictType(
//        keyType = keyType.evaluate(
//            declarationScope = declarationScope,
//        ) as? PrimitiveType ?: throw TypeErrorException(
//            location = keyType.location,
//            message = "Dict key type is not primitive",
//        ),
//        valueType = valueType.evaluateAsType(
//            declarationScope = declarationScope,
//        ),
//    )
}
