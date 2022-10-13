package sigma

data class UndefinedValue(
    val name: Value,
) : Value() {
    init {
        if (Symbol("if") == name) {
            println("if...")
        }
    }

    object IsUndefined : ComputableFunctionValue() {
        override fun apply(
            argument: Value,
        ): Value = BoolValue(argument is UndefinedValue)

        override fun dump(): String = "(isUndefined)"
    }

    override fun isSame(other: Value): Boolean = other is UndefinedValue

    override fun dump(): String = "undefined"
}
