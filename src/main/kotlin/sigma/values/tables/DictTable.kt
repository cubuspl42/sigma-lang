package sigma.values.tables

import sigma.Thunk
import sigma.expressions.Expression
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.Value

class DictTable(
    private val associations: Map<PrimitiveValue, Thunk>,
) : Table() {
    override fun read(
        argument: Value,
    ): Thunk? = associations[argument]

    override fun dumpContent(): String? {
        val entries = associations.mapValues { (_, image) ->
            image.obtain()
        }.entries

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = dumpKey(key = it.key)
            val imageStr = it.value.obtain().dump()

            "$keyStr = $imageStr"
        }
    }

    private fun dumpKey(
        key: Value,
    ): String = when (key) {
        is Symbol -> key.dump()
        else -> "[${key.dump()}]"
    }
}