package sigma

data class ExpressionTable(
    val entries: Map<Value, Expression>,
) {
    fun read(
        argument: Value,
    ): Expression? = entries[argument]

    fun getEntries(
        environment: Table,
    ): Set<Map.Entry<Value, Value>> = entries.mapValues { (_, image) ->
        image.evaluate(context = environment)
    }.entries
}
