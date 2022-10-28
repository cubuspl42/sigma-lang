package sigma.expressions

import sigma.values.tables.Table
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.DictArrayAltContext
import sigma.parser.antlr.SigmaParser.DictContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable

data class DictConstructor(
    val content: Map<Expression, Expression>,
) : Expression {
    companion object {
        val empty: DictConstructor = of(entries = emptyMap())

        fun of(
            entries: Map<Expression, Expression>,
        ): DictConstructor = DictConstructor(
            content = entries,
        )

        fun build(
            ctx: DictContext,
        ): DictConstructor = object : SigmaParserBaseVisitor<DictConstructor>() {
            override fun visitDictTableAlt(
                ctx: SigmaParser.DictTableAltContext,
            ): DictConstructor = DictConstructor(
                content = buildFromTable(ctx.table()),
            )

            override fun visitDictArrayAlt(
                ctx: DictArrayAltContext,
            ): DictConstructor = DictConstructor(
                content = buildFromArray(ctx.content)
            )
        }.visit(ctx)

        private fun buildFromTable(
            ctx: SigmaParser.TableContext,
        ): Map<Expression, Expression> = ctx.tableBind().associate { buildEntry(it) }

        private fun buildFromArray(
            ctx: SigmaParser.ArrayContext,
        ): Map<Expression, Expression> = ctx.bindImage().withIndex().associate { (index, imageCtx) ->
            IntLiteral.of(index) to Expression.build(imageCtx.image)
        }

        private fun buildEntry(
            ctx: SigmaParser.TableBindContext,
        ): Pair<Expression, Expression> = object : SigmaParserBaseVisitor<Pair<Expression, Expression>>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = SymbolLiteral.build(ctx.name) to Expression.build(ctx.image.image)

            override fun visitArbitraryBindAlt(
                ctx: SigmaParser.ArbitraryBindAltContext,
            ) = Expression.build(ctx.key) to Expression.build(ctx.image.image)
        }.visit(ctx)
    }

    override fun dump(): String = "(dict constructor)"

    override fun evaluate(
        context: Table,
    ): DictTable = DictTable(
        environment = context,
        associations = content.mapKeys {
            it.key.evaluate(context = context) as PrimitiveValue
        },
    )
}
