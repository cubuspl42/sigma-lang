package sigma

import sigma.parser.antlr.SigmaParser.DictContext

data class DictConstructor(
    val content: TableConstructor,
) : Expression {
    companion object {
        fun of(
            entries: Map<Expression, Expression>,
        ): DictConstructor = DictConstructor(
            content = TableConstructor(
                entries = entries,
            ),
        )

        fun build(
            ctx: DictContext,
        ): DictConstructor = DictConstructor(
            content = TableConstructor.build(ctx.table()),
        )
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
