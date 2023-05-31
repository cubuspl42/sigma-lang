package sigma.semantics.types

sealed class FunctionType : Type() {
    // This can be improved
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = AnyType

    abstract override fun substituteTypeVariables(resolution: TypeVariableResolution): FunctionType

    abstract val argumentType: Type
    abstract val imageType: Type
}
