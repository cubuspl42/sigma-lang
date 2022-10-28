package sigma

object EmptyTable : Table() {
    override fun read(argument: Value): Value? = null

    override fun dumpContent(): String? = null
}
