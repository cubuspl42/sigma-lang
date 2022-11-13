package sigma.values.tables

import sigma.Thunk
import sigma.values.IntValue
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.Value

data class DictTable(
    // Thought: Rename to entries for consistency? Or the other way around...
    private val associations: Map<PrimitiveValue, Thunk>,
) : Table() {
    companion object {
        val Empty = DictTable(
            associations = emptyMap(),
        )
    }

    override fun read(
        argument: Value,
    ): Thunk? = associations[argument]

    override fun dumpContent(): String? {
        val entries = associations.mapValues { (_, image) ->
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
    associations = elements.withIndex().associate { (index, thunk) ->
        IntValue(index) to thunk
    },
)
