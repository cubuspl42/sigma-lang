package sigma.values.tables

import sigma.values.Symbol
import sigma.values.Value

abstract class Scope : Table() {
    final override fun read(argument: Value): Value? {
        if (argument !is Symbol) return null

        return get(name = argument)
    }

    abstract fun get(name: Symbol): Value?
}
