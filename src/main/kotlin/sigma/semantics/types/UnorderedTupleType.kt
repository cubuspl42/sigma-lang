package sigma.semantics.types

import sigma.SyntaxValueScope
import sigma.semantics.expressions.Abstraction
import sigma.values.Symbol

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
data class UnorderedTupleType(
    val valueTypeByName: Map<Symbol, Type>,
) : TupleType() {
    companion object {
        val Empty = UnorderedTupleType(
            valueTypeByName = emptyMap(),
        )
    }

    override fun dump(): String {
        val dumpedEntries = valueTypeByName.map { (name, valueType) ->
            "(${name.dump()}): ${valueType.dump()}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    fun getFieldType(key: Symbol): Type? {
        return valueTypeByName[key];
    }

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: Type
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = valueTypeByName.isEmpty()

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        if (assignedType !is UnorderedTupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-(unordered tuple) is assigned",
        )

        return valueTypeByName.entries.fold(
            initial = TypeVariableResolution.Empty,
        ) { accumulatedResolution, (key, valueType) ->
            val assignedValueType = assignedType.valueTypeByName[key] ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple lacks key $key",
            )

            val valueResolution = valueType.resolveTypeVariables(
                assignedType = assignedValueType,
            )

            accumulatedResolution.mergeWith(valueResolution)
        }
    }

    override fun toStaticValueScope(): SyntaxValueScope = object : SyntaxValueScope {
        override fun getValueType(valueName: Symbol): Type? = valueTypeByName[valueName]
    }

    override fun toArgumentDeclarationBlock(): Abstraction.ArgumentDeclarationBlock = Abstraction.ArgumentDeclarationBlock(
        argumentDeclarations = valueTypeByName.map { (name, type) ->
            Abstraction.ArgumentDeclaration(
                name = name,
                type = type,
            )
        },
    )

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): UnorderedTupleType = UnorderedTupleType(
        valueTypeByName = valueTypeByName.mapValues { (_, valueType) ->
            valueType.substituteTypeVariables(
                resolution = resolution,
            )
        },
    )
}
