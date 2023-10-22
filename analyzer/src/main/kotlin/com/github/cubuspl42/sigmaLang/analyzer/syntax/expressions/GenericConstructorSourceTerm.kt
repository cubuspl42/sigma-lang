package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class GenericConstructorSourceTerm(
    override val location: SourceLocation,
    override val metaArgumentType: TupleTypeConstructorTerm,
    override val body: ExpressionTerm,
) : ExpressionSourceTerm(), GenericConstructorTerm {
    companion object {
        fun build(
            ctx: SigmaParser.GenericConstructorContext,
        ): GenericConstructorSourceTerm = GenericConstructorSourceTerm(
            location = SourceLocation.build(ctx),
            metaArgumentType = TupleTypeConstructorSourceTerm.build(ctx.metaArgument),
            body = ExpressionSourceTerm.build(ctx.body)
        )
    }

    override fun dump(): String = "${metaArgumentType.dump()} !=> ${body.dump()}"
}
