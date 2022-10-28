package sigma.values.tables

import sigma.Thunk
import sigma.values.Symbol
import sigma.values.Value

abstract class Scope : Table() {
    final override fun read(argument: Value): Thunk? {
        if (argument !is Symbol) return null

        return get(name = argument)
    }

    abstract fun get(name: Symbol): Thunk?
}
