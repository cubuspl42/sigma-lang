package sigma

sealed interface Value

data class ObjectValue(
    private val entries: Map<Value, Value>,
) : Value {
    companion object {
        val empty = ObjectValue(entries = emptyMap())
    }

    fun getValue(key: Value): Value? = entries[key]
}

data class IdentifierValue(
    private val value: String,
) : Value
