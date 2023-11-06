package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.CallableExpressionMethodCallAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class MethodCallSourceTerm(
    override val location: SourceLocation,
    override val self: ExpressionTerm,
    override val method: ReferenceTerm,
    override val argument: TupleConstructorTerm,
) : CallSourceTerm(), MethodCallTerm {
    companion object {
        fun build(
            ctx: CallableExpressionMethodCallAltContext,
        ): MethodCallSourceTerm = MethodCallSourceTerm(
            location = SourceLocation.build(ctx),
            self = ExpressionSourceTerm.build(ctx.self),
            method = ReferenceSourceTerm.build(ctx.method),
            argument = TupleConstructorSourceTerm.build(ctx.argument),
        )
    }

    override fun dump(): String = "${self.dump()}:${method.dump()}${argument.dump()}"
}
