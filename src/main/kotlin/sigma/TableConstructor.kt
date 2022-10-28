package sigma

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ArrayContext
import sigma.parser.antlr.SigmaParser.TableBindContext
import sigma.parser.antlr.SigmaParser.TableContext
import sigma.parser.antlr.SigmaParserBaseVisitor

abstract class BindMapConstructor {
    class Bind<out K : Expression>(
        val key: K,
        val image: Expression,
    ) {
        companion object {
            fun build(
                ctx: TableBindContext,
            ): Bind<Expression> = object : SigmaParserBaseVisitor<Bind<Expression>>() {
                override fun visitSymbolBindAlt(
                    ctx: SigmaParser.SymbolBindAltContext,
                ) = Bind<Expression>(
                    key = SymbolLiteral.build(ctx.name),
                    image = Expression.build(ctx.image),
                )

                override fun visitArbitraryBindAlt(
                    ctx: SigmaParser.ArbitraryBindAltContext,
                ) = Bind<Expression>(
                    key = Expression.build(ctx.key),
                    image = Expression.build(ctx.image),
                )
            }.visit(ctx)
        }

        override fun equals(other: Any?): Boolean {
            throw UnsupportedOperationException()
        }

        override fun hashCode(): Int = 0
    }

    companion object {
        fun build(
            ctx: TableContext,
        ): BindMapConstructor = object : BindMapConstructor() {
            override val binds = ctx.tableBind().map {
                Bind.build(it)
            }
        }
    }

    protected abstract val binds: List<Bind<Expression>>
}

// Idea: Rename to BindSequence? Hierarchy (TableBindSequence, ArrayBindSequence) ?
// Idea: Table.buildEntryMap(ctx)?
// Thought: Is this ok that this is a map? Do expressions have identity at this level?
// Thought: Isn't this now 1:1 with DictConstructor?
data class TableConstructor(
    val entries: Map<Expression, Expression>,
) {
    companion object {
        fun build(
            ctx: TableContext,
        ): TableConstructor = TableConstructor(
            entries = ctx.tableBind().associate { buildEntry(it) },
        )

        fun buildFromArray(
            ctx: ArrayContext,
        ): TableConstructor = TableConstructor(
            entries = ctx.bindImage().withIndex().associate { (index, imageCtx) ->
                IntLiteral.of(index) to Expression.build(imageCtx.image)
            },
        )

        private fun buildEntry(
            ctx: TableBindContext,
        ): Pair<Expression, Expression> = object : SigmaParserBaseVisitor<Pair<Expression, Expression>>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = SymbolLiteral.build(ctx.name) to Expression.build(ctx.image.image)

            override fun visitArbitraryBindAlt(
                ctx: SigmaParser.ArbitraryBindAltContext,
            ) = Expression.build(ctx.key) to Expression.build(ctx.image.image)
        }.visit(ctx)
    }

    fun construct(
        environment: Table,
    ): ExpressionTable = ExpressionTable(
        entries = entries.mapKeys {
            it.key.evaluate(context = environment)
        },
    )
}
