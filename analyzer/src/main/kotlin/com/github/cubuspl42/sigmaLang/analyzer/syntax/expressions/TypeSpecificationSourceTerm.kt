package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class TypeSpecificationSourceTerm(
    override val location: SourceLocation,
    override val subject: ExpressionTerm,
    override val argument: TupleConstructorTerm,
) : ExpressionSourceTerm(), TypeSpecificationTerm {
    companion object {
        fun build(
            ctx: SigmaParser.TypeSpecificationAltContext,
        ): TypeSpecificationSourceTerm = TypeSpecificationSourceTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionSourceTerm.build(ctx.callee),
            argument = TupleConstructorSourceTerm.build(ctx.argument),
        )
    }

    override fun dump(): String = "${subject.dump()}!${argument.dump()}"
}
