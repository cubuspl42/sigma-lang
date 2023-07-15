package sigma.semantics

import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type
import sigma.semantics.types.TypeEntity

data class BuiltinTypeDefinition(
    override val name: Symbol,
    override val definedType: Type,
) : TypeDefinition

private val builtinTypeDefinitions = setOf(
    BuiltinTypeDefinition(
        name = Symbol.of("Bool"),
        definedType = BoolType,
    ),
    BuiltinTypeDefinition(
        name = Symbol.of("Int"),
        definedType = IntCollectiveType,
    ),
)

object BuiltinTypeScope : TypeScope {
    override fun getTypeDefinition(
        typeName: Symbol,
    ): TypeDefinition? = builtinTypeDefinitions.singleOrNull {
        it.name == typeName
    }
}
