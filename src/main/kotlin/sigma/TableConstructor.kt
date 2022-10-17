package sigma

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ArrayContext
import sigma.parser.antlr.SigmaParser.BindContext
import sigma.parser.antlr.SigmaParser.DictArrayAltContext
import sigma.parser.antlr.SigmaParser.TableContext
import sigma.parser.antlr.SigmaParserBaseVisitor

abstract class BindMapConstructor {
    class Bind<out K : Expression>(
        val key: K,
        val image: Expression,
    ) {
        companion object {
            fun build(
                ctx: BindContext,
            ): Bind<Expression> = object : SigmaParserBaseVisitor<Bind<Expression>>() {
                override fun visitSymbolBindAlt(
                    ctx: SigmaParser.SymbolBindAltContext,
                ) = Bind<Expression>(
                    key = Symbol.of(ctx.name.text),
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
            override val binds = ctx.bind().map {
                Bind.build(it)
            }
        }
    }

    protected abstract val binds: List<Bind<Expression>>

    fun evaluateKeys(
        environment: Table,
    ): BindMap = BindMap(binds = binds.map {
        Bind(
            key = it.key.evaluate(context = environment),
            image = it.image,
        )
    })
}

// Idea: Rename to BindSequence? Hierarchy (TableBindSequence, ArrayBindSequence) ?
// Idea: Table.buildEntryMap(ctx)?
// Thought: Is this ok that this is a map? Do expressions have identity at this level?
data class TableConstructor(
    val entries: Map<Expression, Expression>,
) {
    companion object {
        fun build(
            ctx: TableContext,
        ): TableConstructor = TableConstructor(
            entries = ctx.bind().associate { buildEntry(it) },
        )

        fun buildFromArray(
            ctx: ArrayContext,
        ): TableConstructor = TableConstructor(
            entries = ctx.bindImage().withIndex().associate { (index, imageCtx) ->
                IntValue(index) to Expression.build(imageCtx.image)
            },
        )

        private fun buildEntry(
            ctx: BindContext,
        ): Pair<Expression, Expression> = object : SigmaParserBaseVisitor<Pair<Expression, Expression>>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = Symbol.of(ctx.name.text) to Expression.build(ctx.image.image)

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

class ExpressionTable(
    private val entries: Map<Value, Expression>,
) {
    fun read(
        argument: Value,
    ): Expression? = entries[argument]

    fun getEntries(
        environment: Table,
    ): Set<Map.Entry<Value, Value>> = entries.mapValues { (_, image) ->
        image.evaluate(context = environment)
    }.entries

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int = 0
}

class BindMap(
    override val binds: List<Bind<Value>>,
) : BindMapConstructor() {
    companion object {
        fun of(
            vararg binds: Pair<String, Expression>,
        ): BindMap = BindMap(
            binds = binds.map { (name, image) ->
                Bind(
                    key = Symbol.of(name),
                    image = image,
                )
            },
        )
    }

    fun project(key: Value): Expression? {
        val matchingBinds = binds.filter { it.key.isSame(key) }

        if (matchingBinds.isEmpty()) return null

        return matchingBinds.singleOrNull()?.image
            ?: throw IllegalStateException("Multiple binds match the key ${key.dump()}")
    }

    fun encloseImages(
        environment: Table,
    ): Map<Value, Thunk> = binds.associate {
        it.key to it.image.enclose(environment = environment)
    }
}
