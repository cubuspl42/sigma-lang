package sigma

import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.DictArrayAltContext
import sigma.parser.antlr.SigmaParser.DictContext
import sigma.parser.antlr.SigmaParserBaseVisitor

data class DictConstructor(
    val content: TableConstructor,
) : Expression {
    companion object {
        val empty: DictConstructor = of(entries = emptyMap())

        fun of(
            entries: Map<Expression, Expression>,
        ): DictConstructor = DictConstructor(
            content = TableConstructor(
                entries = entries,
            ),
        )

        fun build(
            ctx: DictContext,
        ): DictConstructor = object : SigmaParserBaseVisitor<DictConstructor>() {
            override fun visitDictTableAlt(
                ctx: SigmaParser.DictTableAltContext,
            ): DictConstructor = DictConstructor(
                content = TableConstructor.build(ctx.table()),
            )

            override fun visitDictArrayAlt(
                ctx: DictArrayAltContext,
            ): DictConstructor = DictConstructor(
                content = TableConstructor.buildFromArray(ctx.content)
            )
        }.visit(ctx)
    }

    override fun dump(): String = "(dict constructor)"

    override fun evaluate(
        context: Table,
    ): DictAssociativeTable = DictAssociativeTable(
        environment = context,
        associations = content.construct(
            environment = context,
        ),
    )
}
