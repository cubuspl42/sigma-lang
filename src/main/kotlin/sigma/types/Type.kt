package sigma.types

sealed interface Type {
    fun dump(): String
}

object UndefinedType : Type {
    override fun dump(): String = "Undefined"
}

object BoolType : Type {
    override fun dump(): String = "Bool"
}

object IntType : Type {
    override fun dump(): String = "Int"
}

object SymbolType : Type {
    override fun dump(): String = "Symbol"
}
