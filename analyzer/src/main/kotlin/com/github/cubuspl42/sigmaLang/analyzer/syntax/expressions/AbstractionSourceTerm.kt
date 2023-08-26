package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.AbstractionContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class AbstractionSourceTerm(
    override val location: SourceLocation,
    override val genericParametersTuple: GenericParametersTuple? = null,
    override val argumentType: TupleTypeConstructorTerm,
    override val declaredImageType: ExpressionTerm? = null,
    override val image: ExpressionTerm,
) : ExpressionSourceTerm(), AbstractionTerm {
    companion object {
        fun build(
            ctx: AbstractionContext,
        ): AbstractionSourceTerm = AbstractionSourceTerm(
            location = SourceLocation.build(ctx),
            genericParametersTuple = ctx.genericParametersTuple()?.let {
                GenericParametersTuple.build(it)
            },
            argumentType = ctx.argumentType.let {
                TupleTypeConstructorSourceTerm.build(it)
            },
            declaredImageType = ctx.imageType?.let {
                ExpressionSourceTerm.build(it)
            },
            image = ExpressionSourceTerm.build(ctx.image),
        )
    }

    override fun dump(): String = "(abstraction)"
}
