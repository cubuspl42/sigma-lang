package sigma.semantics.types

data class OrderedTypeTuple(
    val elements: List<TypeEntity>,
) : TypeEntity()
