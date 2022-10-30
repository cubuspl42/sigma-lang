package sigma

import sigma.types.IntType
import sigma.types.SymbolType
import sigma.types.Type
import sigma.values.Symbol

private val builtinTypes = mapOf(
    Symbol.of("Int") to IntType,
    Symbol.of("Symbol") to SymbolType,
)

// Idea: BuiltinScope implementing both StaticValueScope and Scope?
val GlobalStaticScope = StaticScope(
    typeScope = object : StaticTypeScope {
        override fun getType(
            typeName: Symbol,
        ): Type? = builtinTypes[typeName]
    },
    valueScope = BuiltinScope,
)
