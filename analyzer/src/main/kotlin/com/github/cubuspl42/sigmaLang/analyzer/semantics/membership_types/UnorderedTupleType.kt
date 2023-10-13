package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
abstract class UnorderedTupleType : TupleType() {
    abstract val valueTypeThunkByName: Map<Symbol, Thunk<MembershipType>>

    val valueTypeByName by lazy {
        valueTypeThunkByName.mapValues { (_, thunk) ->
            thunk.value ?: throw IllegalStateException("Unable to evaluate the thunk")
        }
    }

    val keys: Set<Symbol>
        get() = valueTypeThunkByName.keys

    data class Entry(
        val name: Symbol,
        val typeThunk: Thunk<MembershipType>,
    )

    data class UnorderedTupleMatch(
        val valuesMatches: Map<Symbol, ValueMatchResult>,
    ) : MembershipType.PartialMatch() {
        sealed class ValueMatchResult {
            abstract fun isFull(): Boolean

            abstract fun dump(): String
        }

        data class PresentValueMatch(
            val valueMatch: MembershipType.MatchResult,
        ) : ValueMatchResult() {
            override fun isFull(): Boolean = valueMatch.isFull()

            override fun dump(): String = valueMatch.dump()
        }

        data object AbsentValueMismatch : ValueMatchResult() {
            override fun isFull(): Boolean = false

            override fun dump(): String = "absent value"
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
        val Empty = object : UnorderedTupleType() {
            override val valueTypeThunkByName: Map<Symbol, Thunk<MembershipType>> = emptyMap()
        }

        fun fromEntries(
            entries: Iterable<Entry>,
        ): UnorderedTupleType = object : UnorderedTupleType() {
            override val valueTypeThunkByName = entries.associate { it.name to it.typeThunk }
        }
    }

    override fun dumpDirectly(depth: Int): String {
        val dumpedEntries = valueTypeByName.map { (name, valueType) ->
            "(${name.dump()}): ${valueType.dumpRecursively(depth = depth + 1)}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    fun getFieldType(key: Symbol): MembershipType? {
        return valueTypeByName[key];
    }

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: MembershipType
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = valueTypeThunkByName.isEmpty()

    override fun resolveTypeVariablesShape(assignedType: MembershipType): TypeVariableResolution {
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

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (assignedType) {
        is UnorderedTupleType -> UnorderedTupleMatch(
            valuesMatches = valueTypeByName.mapValues { (name, valueType) ->
                val assignedValueType = assignedType.getFieldType(key = name)

                when {
                    assignedValueType != null -> UnorderedTupleMatch.PresentValueMatch(
                        valueMatch = valueType.match(
                            assignedType = assignedValueType,
                        )
                    )

                    else -> UnorderedTupleMatch.AbsentValueMismatch
                }
            },
        )

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = valueTypeByName.values.asSequence().flatMap { it.walk() }

    override fun toArgumentDeclarationBlock(): AbstractionConstructor.ArgumentStaticBlock =
        AbstractionConstructor.ArgumentStaticBlock(
            argumentDeclarations = valueTypeByName.map { (name, type) ->
                AbstractionConstructor.ArgumentDeclaration(
                    name = name,
                    annotatedType = type,
                )
            }.toSet(),
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

    override fun toArgumentScope(argument: DictValue): DynamicScope = object : DynamicScope {
        override fun getValue(
            name: Declaration,
        ): Thunk<Value>? = argument.read(name.name)
    }

    override fun isNonEquivalentToDirectly(
        innerContext: NonEquivalenceContext,
        otherType: MembershipType,
    ): Boolean {
        if (otherType !is UnorderedTupleType) return true

        if (keys != otherType.keys) return true

        return keys.any { key ->
            val thisValueType = valueTypeByName[key] ?: return true
            val otherValueType = otherType.valueTypeByName[key] ?: return true

            thisValueType.isNonEquivalentToRecursively(
                outerContext = innerContext,
                otherType = otherValueType,
            )
        }
    }

    override val typeVariableDefinitions: Set<TypeVariableDefinition>
        get() = valueTypeByName.mapNotNull { (name, type) ->
            if (type is TypeType) {
                TypeVariableDefinition(
                    name = name,
                )
            } else null
        }.toSet()
}

fun UnorderedTupleType(
    valueTypeByName: Map<Symbol, MembershipType>,
): UnorderedTupleType = object : UnorderedTupleType() {
    override val valueTypeThunkByName = valueTypeByName.mapValues { (_, type) ->
        Thunk.pure(type)
    }
}
