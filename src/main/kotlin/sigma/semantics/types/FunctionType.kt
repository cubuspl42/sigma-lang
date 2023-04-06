package sigma.semantics.types

sealed class FunctionType : Type() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = AnyType

    abstract val argumentType: Type
    abstract val imageType: Type
}
