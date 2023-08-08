package sigma.evaluation.values

data class Symbol(
    val name: String,
) : PrimitiveValue() {
    companion object {
        fun of(
            name: String,
        ): Symbol = Symbol(
            name = name,
        )
    }

    override fun dump(): String = "`$name`"
}
