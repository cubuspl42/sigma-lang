package sigma

import sigma.values.Symbol

interface TypeContext {
    fun getType(name: Symbol): Type?
}

object GlobalTypeContext : TypeContext {
    private val builtinTypes = mapOf(
        Symbol.of("Int") to IntType,
    )

    override fun getType(
        name: Symbol,
    ): Type? = builtinTypes[name]
}
