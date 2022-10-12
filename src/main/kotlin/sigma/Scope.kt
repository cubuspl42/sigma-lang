package sigma

abstract class Scope {
    object Empty : Scope() {
        override fun get(name: String): Value {
            throw IllegalStateException()
        }
    }

    fun extend(
        label: String,
        value: Value,
    ): Scope {
        val parent = this

        return object : Scope() {
            override fun get(name: String): Value = when (label) {
                name -> value
                else -> parent.get(name = name)
            }
        }
    }

    abstract fun get(name: String): Value
}
