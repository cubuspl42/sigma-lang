package sigma.types

sealed class FunctionType : Type() {
    abstract val argumentType: Type
    abstract val imageType: Type
}

data class AbstractionType(
    override val imageType: Type,
) : FunctionType() {
    override val argumentType: Type = UndefinedType

    override fun isAssignableTo(otherType: Type): Boolean {
        TODO("Not yet implemented")
    }

    override fun dump(): String = "Abstraction"
}