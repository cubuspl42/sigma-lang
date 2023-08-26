package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class GenericTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val genericParametersTuple: GenericParametersTuple,
    override val body: ExpressionTerm,
) : ExpressionSourceTerm(), GenericTypeConstructorTerm {
//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): GenericTypeConstructor = GenericTypeConstructor(
//        context = declarationScope,
//        argumentMetaType = genericParametersTuple,
//        bodyTerm = body,
//        body = body.evaluate(
//            declarationScope = genericParametersTuple.asDeclarationBlock.chainWith(
//                outerScope = declarationScope,
//            ),
//        ),
//    )

    companion object {
        fun build(
            ctx: SigmaParser.GenericTypeConstructorContext,
        ): ExpressionSourceTerm = GenericTypeConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = GenericParametersTuple.build(ctx.genericParametersTuple()),
            body = build(ctx.body),
        )
    }

    override fun dump(): String = "(generic type constructor)"
}
