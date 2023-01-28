package sigma.types

sealed class FunctionType : Type() {
    abstract val argumentType: Type
    abstract val imageType: Type
}

data class AbstractionType(
    override val argumentType: TupleType,
    override val imageType: Type,
) : FunctionType() {
    override fun dump() = "${argumentType.dump()} -> ${imageType.dump()}"
}
