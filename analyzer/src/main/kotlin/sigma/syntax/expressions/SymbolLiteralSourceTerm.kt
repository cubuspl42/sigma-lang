package sigma.syntax.expressions


import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

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
