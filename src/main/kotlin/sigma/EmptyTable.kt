package sigma

object EmptyTable : Table() {
    override fun read(argument: Value): Value? = null

    override fun isSubsetOf(other: FunctionValue): Boolean = true

    override fun dumpContent(): String? = null
}
