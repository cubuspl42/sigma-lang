package sigma.syntax.expressions


import sigma.parser.antlr.SigmaParser.SymbolLiteralAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.scope.Scope

data class SymbolLiteralTerm(
    override val location: SourceLocation,
    val symbol: Symbol,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: SymbolLiteralAltContext,
        ): SymbolLiteralTerm = SymbolLiteralTerm(
            location = SourceLocation.build(ctx),
            symbol = Symbol(
                name = ctx.text.drop(1).dropLast(1),
            ),
        )
    }

    override fun dump(): String = symbol.dump()
}
