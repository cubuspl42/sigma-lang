package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

data class ArrayTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val elementType: ExpressionSourceTerm,
) : ExpressionSourceTerm(), ArrayTypeConstructorTerm {
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
