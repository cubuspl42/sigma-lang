package sigma

import sigma.parser.antlr.SigmaParser.AbstractionAltContext
import sigma.parser.antlr.SigmaParser.DictAltContext
import sigma.parser.antlr.SigmaParser.SymbolAltContext
import sigma.parser.antlr.SigmaParser.ValueContext
import sigma.parser.antlr.SigmaParserBaseVisitor

sealed class Value : Expression {
    companion object {
        fun build(
            value: ValueContext,
        ): Value = object : SigmaParserBaseVisitor<Value>() {
            override fun visitDictAlt(
                ctx: DictAltContext,
            ): Value = Dict.build(ctx.dict())

            override fun visitSymbolAlt(
                ctx: SymbolAltContext,
            ): Value = Symbol.build(ctx.symbol())

            override fun visitAbstractionAlt(
                ctx: AbstractionAltContext,
            ): Value = Abstraction.build(ctx.abstraction())
        }.visit(value)
    }

    override fun evaluate(scope: Scope): Value = this

    abstract fun apply(
        scope: Scope = Scope.Empty,
        key: Value,
    ): Value
}
