package sigma

abstract class AssociativeTable(
    private val associations: ExpressionTable,
) : Table() {
    override fun read(
        argument: Value,
    ): Value? = associations.read(argument)?.evaluate(
        context = environment,
    )

    override fun dumpContent(): String? {
        val entries = associations.getEntries(environment = environment)

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = dumpKey(key = it.key)
            val imageStr = it.value.evaluate(context = environment).dump()

            "$keyStr = $imageStr"
        }
    }

    private fun dumpKey(
        key: Value,
    ): String = when (key) {
        is Symbol -> key.dump()
        else -> "[${key.dump()}]"
    }

    abstract val environment: Table
}
