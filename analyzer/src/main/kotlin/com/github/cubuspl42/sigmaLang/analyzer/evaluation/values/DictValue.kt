package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

data class DictValue(
    val thunkByKey: Map<PrimitiveValue, Thunk<Value>>,
) : FunctionValue() {
    val thunkByValue: Map<PrimitiveValue, Value>
        get() = thunkByKey.mapValues { (_, thunk) -> thunk.value!! }

    val entries: Collection<Entry>
        get() = thunkByKey.entries.map { (key, valueThunk) ->
            Entry(
                key = key,
                valueThunk = valueThunk,
            )
        }

    data class Entry(
        val key: PrimitiveValue,
        val valueThunk: Thunk<Value>,
    )

    companion object {
        fun fromMap(
            entries: Map<PrimitiveValue, Value>,
        ): DictValue = DictValue(
            thunkByKey = entries.mapValues { (_, value) ->
                Thunk.pure(value)
            },
        )

        fun fromList(
            list: List<Thunk<Value>>,
        ): DictValue = DictValue(
            thunkByKey = list.withIndex().associate { (index, element) ->
                IntValue(value = index.toLong()) to element
            },
        )

        @JvmName("fromListValue")
        fun fromList(
            list: List<Value>,
        ): DictValue = fromList(
            list = list.map { Thunk.pure(it) },
        )

        fun fromEntries(
            entries: Iterable<Entry>,
        ): DictValue = DictValue(
            thunkByKey = entries.associate { it.key to it.valueThunk },
        )

        val Empty = DictValue(
            thunkByKey = emptyMap(),
        )
    }

    override fun apply(
        argument: Value,
    ): Thunk<Value> = read(
        key = argument,
    ) ?: Thunk.pure(
        UndefinedValue.withName(
            name = argument,
        )
    )

    override fun dump(): String {
        val content = dumpContent()

        return when {
            content != null -> "{ $content }"
            else -> "∅"
        }
    }

    fun read(
        key: Value,
    ): Thunk<Value>? = thunkByKey[key as PrimitiveValue]

    fun readValue(
        key: Value,
    ): Value? = read(key = key)?.let {
        it.value!!
    }

    private fun dumpContent(): String? {
        val entries = thunkByKey.mapValues { (_, image) ->
            image
        }.entries

        if (entries.isEmpty()) return null

        return entries.joinToString(separator = ", ") {
            val keyStr = dumpKey(key = it.key)
            val imageStr = it.value.value?.dump() ?: "(error)"

            "$keyStr = $imageStr"
        }
    }

    private fun dumpKey(
        key: Value,
    ): String = when (key) {
        is Identifier -> key.dump()
        else -> "[${key.dump()}]"
    }

    private fun toMapDebug(): Map<Long, Value> = thunkByKey.map { (key, value) ->
        (key as IntValue).value to value.value!!
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
    elements: List<Thunk<Value>>,
): DictValue = DictValue(
    thunkByKey = elements.withIndex().associate { (index, value) ->
        IntValue(value = index.toLong()) to value
    },
)

@JvmName("ArrayTableValue")
@Suppress("FunctionName")
fun ArrayTable(
    elements: List<Value>,
): DictValue = ArrayTable(
    elements = elements.map {
        Thunk.pure(it)
    },
)

