package sigma.expressions

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.DictArrayAltContext
import sigma.parser.antlr.SigmaParser.DictContext
import sigma.parser.antlr.SigmaParserBaseVisitor
import sigma.values.PrimitiveValue
import sigma.values.tables.DictTable
import sigma.values.tables.Scope
import sigma.types.DictType
import sigma.types.Type
import sigma.values.Symbol

data class DictConstructor(
    val content: List<Assignment>,
) : Expression() {
    sealed interface Assignment {
        val key: Expression
        val value: Expression
    }

    data class SymbolAssignment(
        val name: Symbol,
        override val value: Expression,
    ) : Assignment {
        override val key: SymbolLiteral = SymbolLiteral(symbol = name)
    }

    data class ArbitraryAssignment(
        override val key: Expression,
        override val value: Expression,
    ) : Assignment

    companion object {
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
        ): List<Assignment> = ctx.tableBind().map {
            buildAssignment(it)
        }

        private fun buildFromArray(
            ctx: SigmaParser.ArrayContext,
        ): List<ArbitraryAssignment> = ctx.bindImage().withIndex().map { (index, imageCtx) ->
            // Note: When arbitrary binds aren't supported, then these int literals can be changed to values
            ArbitraryAssignment(
                key = IntLiteral.of(index),
                value = Expression.build(imageCtx.image),
            )
        }

        private fun buildAssignment(
            ctx: SigmaParser.TableBindContext,
        ): Assignment = object : SigmaParserBaseVisitor<Assignment>() {
            override fun visitSymbolBindAlt(
                ctx: SigmaParser.SymbolBindAltContext,
            ) = SymbolAssignment(
                name = Symbol.of(ctx.name.text),
                value = Expression.build(ctx.image.image),
            )

            override fun visitArbitraryBindAlt(
                ctx: SigmaParser.ArbitraryBindAltContext,
            ) = ArbitraryAssignment(
                key = Expression.build(ctx.key),
                value = Expression.build(ctx.image.image),
            )
        }.visit(ctx)
    }

    override fun dump(): String = "(dict constructor)"

    override fun inferType(): Type = DictType

    override fun evaluate(
        context: Scope,
    ): DictTable = DictTable(
        associations = content.associate {
            val key = it.key.evaluate(context = context).obtain() as PrimitiveValue
            val value = it.value.bind(scope = context)

            key to value
        },
    )
}
