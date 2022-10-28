package sigma.values.tables

import sigma.expressions.Expression
import sigma.values.Symbol
import sigma.values.Value

abstract class AssociativeTable(
    private val associations: Map<Value, Expression>,
) : Table() {
    override fun read(
        argument: Value,
    ): Value? = associations[argument]?.evaluate(
        context = environment,
    )

    override fun dumpContent(): String? {
        val entries = associations.mapValues { (_, image) ->
            image.evaluate(context = environment)
        }.entries

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = dumpKey(key = it.key)
            val imageStr = it.value.dump()

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
