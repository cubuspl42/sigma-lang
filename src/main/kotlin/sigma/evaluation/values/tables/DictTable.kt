package sigma.evaluation.values.tables

import sigma.Thunk
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value

data class DictTable(
    private val entries: Map<PrimitiveValue, Thunk>,
) : Table() {
    companion object {
        fun fromList(
            list: List<Value>,
        ): DictTable = DictTable(
            entries = list.withIndex().associate { (index, element) ->
                IntValue(value = index.toLong()) to element
            },
        )

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

    override fun equalsTo(other: Value): Boolean {
        if (other !is DictTable) return  false

        return this.entries.keys == other.entries.keys && entries.all { (key, value) ->
            val otherValue = other.read(key)!!

            value.toEvaluatedValue.equalsTo(otherValue.toEvaluatedValue)
        }
    }

    private fun dumpKey(
        key: Value,
    ): String = when (key) {
        is Symbol -> key.dump()
        else -> "[${key.dump()}]"
    }

    fun toMapDebug(): Map<Long, Thunk> = entries.map { (key, value) ->
        (key as IntValue).value to value
    }.toMap()

    fun toListDebug(): List<Thunk> {
        val map = toMapDebug()

        val expectedIndexSet = (0 until map.size).toSet()

        if (map.keys != expectedIndexSet) throw Exception("Dict keys are not consecutive")

        return map.entries.sortedBy { it.key }.map { it.value }
    }
}

@Suppress("FunctionName")
fun ArrayTable(
    elements: List<Thunk>,
) = DictTable(
    entries = elements.withIndex().associate { (index, thunk) ->
        IntValue(value = index.toLong()) to thunk
    },
)