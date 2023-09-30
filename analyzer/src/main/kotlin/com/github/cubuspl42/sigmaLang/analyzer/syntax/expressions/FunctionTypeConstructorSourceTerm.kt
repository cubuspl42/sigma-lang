package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.FunctionTypeConstructorContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class FunctionTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val metaArgumentType: TupleTypeConstructorTerm?,
    override val argumentType: TupleTypeConstructorTerm,
    override val imageType: ExpressionTerm,
) : ExpressionSourceTerm(), FunctionTypeConstructorTerm {
    companion object {
        fun build(
            ctx: FunctionTypeConstructorContext,
        ): FunctionTypeConstructorSourceTerm {


            return FunctionTypeConstructorSourceTerm(
                location = SourceLocation.build(ctx),
                metaArgumentType = ctx.metaArgumentType()?.let {
                    TupleTypeConstructorSourceTerm.build(it.tupleTypeConstructor())
                },
                argumentType = ctx.argumentType.let {
                    TupleTypeConstructorSourceTerm.build(it)
                },
                imageType = ExpressionSourceTerm.build(ctx.imageType),
            )
        }
    }

    override fun dump(): String = "(function type constructor)"

//    override fun evaluate(
//        declarationScope: StaticScope,
//    ): TypeEntity {
//        val innerDeclarationScope = genericParametersTuple?.asDeclarationBlock?.chainWith(
//            outerScope = declarationScope,
//        ) ?: declarationScope
//
//        val argumentType = argumentType.evaluate(
//            declarationScope = innerDeclarationScope,
//        )
//
//        val imageType = this.imageType.evaluateAsType(
//            declarationScope = innerDeclarationScope,
//        )
//
//        return UniversalFunctionType(
//            argumentType = argumentType,
//            imageType = imageType,
//        )
//    }
}
