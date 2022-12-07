package sigma.types

@Suppress("FunctionName")
fun ArrayType(
    elementType: Type,
) = DictType(
    keyType = IntCollectiveType,
    valueType = elementType,
)
