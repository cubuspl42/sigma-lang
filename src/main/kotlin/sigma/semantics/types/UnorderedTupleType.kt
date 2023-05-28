package sigma.semantics.types

import sigma.SyntaxValueScope
import sigma.evaluation.values.Symbol
import sigma.semantics.expressions.Abstraction

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
data class UnorderedTupleType(
    val valueTypeByName: Map<Symbol, Type>,
) : TupleType() {
    data class UnorderedTupleMatch(
        val valuesMatches: Map<Symbol, ValueMatchResult>,
    ) : Type.PartialMatch() {
        sealed class ValueMatchResult {
            abstract fun isFull(): Boolean

            abstract fun dump(): String
        }

        data class PresentValueMatch(
            val valueMatch: Type.MatchResult,
        ) : ValueMatchResult() {
            override fun isFull(): Boolean = valueMatch.isFull()

            override fun dump(): String = valueMatch.dump()
        }

        object AbsentValueMismatch : ValueMatchResult() {
            override fun isFull(): Boolean = false

            override fun dump(): String = "absent value"

            override fun toString(): String = "AbsentValueMismatch"
        }

        override fun isFull(): Boolean = valuesMatches.values.all { it.isFull() }

        override fun dump(): String {
            val firstMismatch = valuesMatches.entries.firstOrNull { !it.value.isFull() }

            return when {
                firstMismatch != null -> "at key ${firstMismatch.key.dump()}: " + firstMismatch.value.dump()
                else -> "(?)"
            }
        }
    }

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

    override fun match(
        assignedType: Type,
    ): MatchResult = when (assignedType) {
        is UnorderedTupleType -> UnorderedTupleMatch(valuesMatches = valueTypeByName.mapValues { (name, valueType) ->
            val assignedValueType = assignedType.getFieldType(key = name)

            when {
                assignedValueType != null -> UnorderedTupleMatch.PresentValueMatch(
                    valueMatch = valueType.match(
                        assignedType = assignedValueType,
                    )
                )

                else -> UnorderedTupleMatch.AbsentValueMismatch
            }
        })

        else -> Type.TotalMismatch
    }

    override fun toStaticValueScope(): SyntaxValueScope = object : SyntaxValueScope {
        override fun getValueType(valueName: Symbol): Type? = valueTypeByName[valueName]
    }

    override fun toArgumentDeclarationBlock(): Abstraction.ArgumentDeclarationBlock =
        Abstraction.ArgumentDeclarationBlock(
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
