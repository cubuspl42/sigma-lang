package sigma

import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.Type
import sigma.values.Symbol

private val builtinTypes = mapOf(
    Symbol.of("Bool") to BoolType,
    Symbol.of("Int") to IntCollectiveType,
)

val BuiltinTypeScope = object : StaticTypeScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = builtinTypes[typeName]
}
