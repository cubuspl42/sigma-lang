package sigma.types

sealed class FunctionType : Type() {
    abstract val argumentType: Type
    abstract val imageType: Type
}
