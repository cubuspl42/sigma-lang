package sigma

class ArgumentTable(
    private val name: Symbol,
    private val value: Value,
) : Table() {
    override fun read(
        argument: Value,
    ): Value? = value.takeIf { name.isSame(argument) }

    override fun dumpContent(): String = "${name.name} = ${value.dump()} [argument]"
}
