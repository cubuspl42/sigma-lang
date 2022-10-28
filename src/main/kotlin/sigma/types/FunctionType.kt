package sigma.types

data class FunctionType(
    val argumentType: Type,
    val imageType: Type,
) : Type
