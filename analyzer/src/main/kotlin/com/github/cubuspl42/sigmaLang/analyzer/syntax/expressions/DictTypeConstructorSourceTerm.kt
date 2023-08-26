package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class DictTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val keyType: ExpressionTerm,
    override val valueType: ExpressionTerm,
) : ExpressionSourceTerm(), DictTypeConstructorTerm {
    companion object {
        fun build(
            ctx: SigmaParser.DictTypeConstructorContext,
        ): DictTypeConstructorSourceTerm = DictTypeConstructorSourceTerm(
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
