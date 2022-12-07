package sigma.values.tables

import sigma.Thunk
import sigma.values.IntValue
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.Value

data class DictTable(
    private val entries: Map<PrimitiveValue, Thunk>,
) : Table() {
    companion object {
        val Empty = DictTable(
            entries = emptyMap(),
        )
    }

    override fun read(
        argument: Value,
    ): Thunk? = entries[argument as PrimitiveValue]

    override fun dumpContent(): String? {
        val entries = entries.mapValues { (_, image) ->
            image.toEvaluatedValue
        }.entries

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = dumpKey(key = it.key)
            val imageStr = it.value.toEvaluatedValue.dump()

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

@Suppress("FunctionName")
fun ArrayTable(
    elements: List<Thunk>,
) = DictTable(
    entries = elements.withIndex().associate { (index, thunk) ->
        IntValue(index) to thunk
    },
)
