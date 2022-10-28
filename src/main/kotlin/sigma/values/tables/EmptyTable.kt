package sigma.values.tables

import sigma.values.Value

object EmptyTable : Table() {
    override fun read(argument: Value): Value? = null

    override fun dumpContent(): String? = null
}
