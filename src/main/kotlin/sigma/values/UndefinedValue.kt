package sigma.values

class UndefinedValue private constructor(
    val name: Value? = null,
) : Value() {
    companion object {
        fun withName(
            name: Value,
        ): UndefinedValue = UndefinedValue(name = name)
    }

    override fun equals(other: Any?): Boolean = other is UndefinedValue

    override fun equalsTo(other: Value): Boolean = this == other

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = listOf(
        "undefined",
        name?.dump()?.let { "($it)" },
    ).joinToString(separator = " ")

    override fun dump(): String = "undefined"
}
