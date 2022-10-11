package sigma

import sigma.parser.antlr.SigmaParser

data class FormExpression(
    val entries: List<Entry>,
) : Expression {
    data class Entry(
        val key: Expression,
        val value: Expression,
    ) {
        fun dump(): String = "${key.dump()}: ${value.dump()}"
    }

    companion object {
        val empty = FormExpression(
            entries = emptyList(),
        )

        fun build(
            expression: SigmaParser.FormContext,
        ): FormExpression = FormExpression(
            entries = expression.entry().map {
                Entry(
                    key = Expression.build(it.key),
                    value = Expression.build(it.value),
                )
            },
        )
    }

    override fun evaluate(): Value = ObjectValue(
        entries = entries.associate {
            it.key.evaluate() to it.value.evaluate()
        },
    )

    override fun dump(): String = "{${entries.joinToString { it.dump() }}}"
}
