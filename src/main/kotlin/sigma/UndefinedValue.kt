package sigma

class UndefinedValue private constructor(
    val name: Value? = null,
) : Value() {
    companion object {
        val undefined = UndefinedValue(name = null)

        fun withName(
            name: Value,
        ): UndefinedValue = UndefinedValue(name = name)
    }

    object IsUndefined : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Value = BoolValue(argument is UndefinedValue)

        override fun dump(): String = "(isUndefined)"
    }

    override fun equals(other: Any?): Boolean = other is UndefinedValue

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = listOf(
        "undefined",
        name?.dump()?.let { "($it)" },
    ).joinToString(separator = " ")

    override fun dump(): String = "undefined"
}
