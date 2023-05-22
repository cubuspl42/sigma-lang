package sigma

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.Type
import sigma.evaluation.values.Symbol

private val builtinTypes = mapOf(
    Symbol.of("Bool") to BoolType,
    Symbol.of("Int") to IntCollectiveType,
)

val BuiltinTypeScope = object : TypeScope {
    override fun getType(
        typeName: Symbol,
    ): Type? = builtinTypes[typeName]
}
