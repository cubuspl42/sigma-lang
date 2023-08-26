package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallExpressionAltContext
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallExpressionTupleConstructorAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class PostfixCallSourceTerm(
    override val location: SourceLocation,
    // Idea: Rename to `callee`? (again?)
    override val subject: ExpressionTerm,
    override val argument: ExpressionTerm,
) : CallSourceTerm(), PostfixCallTerm {
    companion object {
        fun build(
            ctx: CallExpressionAltContext,
        ): PostfixCallSourceTerm = PostfixCallSourceTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionSourceTerm.build(ctx.callee),
            argument = ExpressionSourceTerm.build(ctx.argument),
        )

        fun build(
            ctx: CallExpressionTupleConstructorAltContext,
        ): PostfixCallSourceTerm = PostfixCallSourceTerm(
            location = SourceLocation.build(ctx),
            subject = ExpressionSourceTerm.build(ctx.callee),
            argument = TupleConstructorSourceTerm.build(ctx.argument),
        )
    }

    override fun dump(): String = "${subject.dump()}[${argument.dump()}]"
}
