package sigma.types

data class FunctionType(
    // TODO
    // val argumentType: Type,
    val imageType: Type,
) : Type {
    override fun dump(): String = "Function"
}
