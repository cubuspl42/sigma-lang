package sigma.semantics

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.evaluation.values.Symbol
import sigma.semantics.types.TypeEntity

private val builtinTypes = mapOf(
    Symbol.of("Bool") to BoolType,
    Symbol.of("Int") to IntCollectiveType,
)

val BuiltinTypeScope = object : TypeScope {
    override fun getTypeEntity(
        typeName: Symbol,
    ): TypeEntity? = builtinTypes[typeName]
}
