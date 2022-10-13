package sigma

data class LinkedScope(
    val parent: Scope,
    val binds: Map<Symbol, Expression>,
) : Scope() {
    override fun dump(): String = "(scope)"

    override fun get(name: Symbol): Value {
        val localValue = binds[name]?.evaluate(scope = this)

        return localValue ?: parent.get(name = name)
    }
}
