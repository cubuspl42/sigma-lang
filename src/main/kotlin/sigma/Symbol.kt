package sigma

data class Symbol(
    val name: String,
) : Value() {
    companion object {
        fun of(
            name: String,
        ): Symbol = Symbol(
            name = name,
        )
    }

    override fun dump(): String = "`$name`"
}
