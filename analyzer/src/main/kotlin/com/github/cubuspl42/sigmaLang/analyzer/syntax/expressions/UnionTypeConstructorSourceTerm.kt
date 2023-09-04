package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

data class UnionTypeConstructorSourceTerm(
    override val location: SourceLocation,
    override val leftType: ExpressionTerm,
    override val rightType: ExpressionTerm,
) : ExpressionSourceTerm(), UnionTypeConstructorTerm {
    companion object {
        fun build(
            ctx: SigmaParser.UnionTypeConstructorAltContext,
        ): UnionTypeConstructorSourceTerm {
            val leftArgument = ExpressionSourceTerm.build(ctx.left)
            val rightArgument = ExpressionSourceTerm.build(ctx.right)

            return UnionTypeConstructorSourceTerm(
                location = SourceLocation.build(ctx),
                leftType = leftArgument,
                rightType = rightArgument,
            )
        }
    }

    override fun dump(): String = "${leftType.dump()} | {${rightType.dump()}"
}
