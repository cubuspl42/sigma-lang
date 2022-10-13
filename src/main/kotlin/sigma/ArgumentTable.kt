package sigma

class ArgumentTable(
    name: Symbol,
    value: Value,
) : AssociativeTable(
    associations = ExpressionTable(
        entries = mapOf(
            name to value,
        ),
    ),
) {
    override val environment: Table = EmptyTable
}
