package sigma

import sigma.parser.antlr.SigmaParser.TableContext

data class TableExpression(
    val label: String? = null,
    val entries: Map<Symbol, Expression>,
) : Expression {
    companion object {
        val empty = TableExpression(
            entries = emptyMap(),
        )

        fun build(
            form: TableContext,
        ): TableExpression = TableExpression(
            label = form.label?.text,
            entries = form.entry().associate {
                Symbol.build(it.argument) to Expression.build(it.image)
            },
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = Table(
        label = label,
        scope = scope,
        entries = entries,
    )

    override fun dump(): String = "{${
        entries.entries.joinToString {
            it.key.toString() + ": " + it.value.dump()
        }
    }}"
}
