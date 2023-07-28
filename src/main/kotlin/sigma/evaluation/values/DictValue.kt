package sigma.evaluation.values

import sigma.semantics.expressions.EvaluationContext

class DictValue(
    val entries: Map<PrimitiveValue, Value>,
) : FunctionValue() {

    companion object {
        fun fromList(
            list: List<Value>,
        ): DictValue = DictValue(
            entries = list.withIndex().associate { (index, element) ->
                IntValue(value = index.toLong()) to element
            },
        )

        val Empty = DictValue(
            entries = emptyMap(),
        )
    }

    override fun apply(
        context: EvaluationContext,
        argument: Value,
    ): EvaluationResult = (read(
        key = argument,
    ) ?: UndefinedValue.withName(
        name = argument,
    )).asEvaluationResult

    override fun dump(): String {
        val content = dumpContent()

        return when {
            content != null -> "{ $content }"
            else -> "∅"
        }
    }

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    fun read(
        key: Value,
    ): Value? = entries[key as PrimitiveValue]

    private fun dumpContent(): String? {
        val entries = entries.mapValues { (_, image) ->
            image
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

    fun toMapDebug(): Map<Long, Value> = entries.map { (key, value) ->
        (key as IntValue).value to value
    }.toMap()

    fun toListDebug(): List<Value> {
        val map = toMapDebug()

        val expectedIndexSet = (0 until map.size).toSet()

        if (map.keys != expectedIndexSet) throw Exception("Dict keys are not consecutive")

        return map.entries.sortedBy { it.key }.map { it.value }
    }
}

@Suppress("FunctionName")
fun ArrayTable(
    elements: List<Value>,
): DictValue = DictValue(
    entries = elements.withIndex().associate { (index, value) ->
        IntValue(value = index.toLong()) to value
    },
)
