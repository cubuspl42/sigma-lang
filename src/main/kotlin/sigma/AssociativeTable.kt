package sigma

abstract class AssociativeTable(
    private val associations: ExpressionTable,
) : Table() {
    override fun read(
        argument: Value,
    ): Value? = associations.read(argument)?.evaluate(
        context = environment,
    )

    override fun isSubsetOf(
        other: FunctionValue,
    ): Boolean = associations.getEntries(
        environment = environment,
    ).all { (key, image) ->
        val otherImage = other.apply(argument = key)

        image.obtain().isSame(otherImage)
    }

    override fun dumpContent(): String? {
        val entries = associations.getEntries(environment = environment)

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = it.key.dump()
            val imageStr = it.value.evaluate(context = environment).dump()

            "$keyStr = $imageStr"
        }
    }

    fun chainWith(
        context: Table,
    ): ChainedTable = ChainedTable(
        context = context,
        table = this,
    )

    abstract val environment: Table
}
