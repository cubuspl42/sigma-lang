package sigma

object BuiltinScope : Scope() {
    private val binds: Map<String, Value> = mapOf(
        "false" to BoolValue.False,
        "true" to BoolValue.True,
        "if" to BoolValue.If,
        "1" to IntValue(1),
        "100" to IntValue(100),
        "plus" to IntValue.Plus,
        "minus" to IntValue.Minus,
        "sq" to IntValue.Sq,
        "eq" to IntValue.Eq,
    )

    override fun get(
        name: Symbol,
    ): Value = binds[name.name]
        ?: throw UnsupportedOperationException("Built-in scope does not contain name ${name.dump()}")

    override fun dump(): String = "(built-in scope)"
}
