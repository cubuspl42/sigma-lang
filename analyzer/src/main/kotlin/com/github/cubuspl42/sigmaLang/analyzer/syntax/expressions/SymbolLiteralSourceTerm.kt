package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions


import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.SymbolLiteralAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class SymbolLiteralSourceTerm(
    override val location: SourceLocation,
    val symbol: Symbol,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: SymbolLiteralAltContext,
        ): SymbolLiteralSourceTerm = SymbolLiteralSourceTerm(
            location = SourceLocation.build(ctx),
            symbol = Symbol(
                name = ctx.text.drop(1).dropLast(1),
            ),
        )
    }

    override fun dump(): String = symbol.dump()
}
