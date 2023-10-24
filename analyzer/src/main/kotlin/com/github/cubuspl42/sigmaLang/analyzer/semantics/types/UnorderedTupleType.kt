package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.OrderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
abstract class UnorderedTupleType : TupleType() {

    abstract val valueTypeThunkByName: Map<Symbol, Thunk<TypeAlike>>

    override val entries: Collection<NamedEntry> by lazy {
        valueTypeThunkByName.map { (name, typeThunk) ->
            UnorderedTupleType.NamedEntry(
                name = name,
                typeThunk = typeThunk,
            )
        }
    }

    fun getValueType(name: Symbol): TypeAlike? = valueTypeThunkByName[name]?.let {
        it.value ?: throw IllegalStateException("Unable to evaluate the value type thunk")
    }

    val valueTypeByName by lazy {
        valueTypeThunkByName.mapValues { (_, thunk) ->
            thunk.value ?: throw IllegalStateException("Unable to evaluate the thunk")
        }
    }

    val keys: Set<Symbol>
        get() = valueTypeThunkByName.keys

    data class NamedEntry(
        override val name: Symbol,
        override val typeThunk: Thunk<TypeAlike>,
    ) : Entry() {
        constructor(
            name: Symbol,
            type: TypeAlike,
        ) : this(
            name = name,
            typeThunk = Thunk.pure(type),
        )
    }

    data class UnorderedTupleMatch(
        val valuesMatches: Map<Symbol, ValueMatchResult>,
    ) : SpecificType.PartialMatch() {
        sealed class ValueMatchResult {
            abstract fun isFull(): Boolean

            abstract fun dump(): String
        }

        data class PresentValueMatch(
            val valueMatch: SpecificType.MatchResult,
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
            override val valueTypeThunkByName: Map<Symbol, Thunk<SpecificType>> = emptyMap()
        }

        fun fromEntries(
            entries: Iterable<NamedEntry>,
        ): UnorderedTupleType = object : UnorderedTupleType() {
            override val valueTypeThunkByName = entries.associate { it.name to it.typeThunk }
        }

        fun fromEntries(
            vararg entries: NamedEntry,
        ): UnorderedTupleType = fromEntries(entries = entries.asIterable())
    }

    override fun dumpDirectly(depth: Int): String {
        val dumpedEntries = valueTypeByName.map { (name, valueType) ->
            "(${name.dump()}): ${valueType.dumpRecursively(depth = depth + 1)}"
        }

        return "{${dumpedEntries.joinToString()}}"
    }

    fun getFieldType(key: Symbol): TypeAlike? = getValueType(name = key)

    override val keyType: PrimitiveType
        get() = TODO("key1 | key2 | ...")

    override val valueType: SpecificType
        get() = TODO("value1 | value2 | ...")

    override fun isDefinitelyEmpty(): Boolean = valueTypeThunkByName.isEmpty()

    override fun resolveTypeVariablesShape(assignedType: TypeAlike): TypePlaceholderResolution {
        if (assignedType !is UnorderedTupleType) throw TypeVariableResolutionError(
            message = "Cannot resolve type variables, non-(unordered tuple) is assigned",
        )

        return valueTypeByName.entries.fold(
            initial = TypePlaceholderResolution.Empty,
        ) { accumulatedResolution, (key, valueType) ->
            val assignedValueType = assignedType.getValueType(name = key) ?: throw TypeVariableResolutionError(
                message = "Cannot resolve type variables, assigned tuple lacks key $key",
            )

            val valueResolution = valueType.resolveTypePlaceholders(
                assignedType = assignedValueType as SpecificType,
            )

            accumulatedResolution.mergeWith(valueResolution)
        }
    }

    override fun matchShape(
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (assignedType) {
        is UnorderedTupleType -> UnorderedTupleMatch(
            valuesMatches = valueTypeByName.mapValues { (name, valueType) ->
                val assignedValueType = assignedType.getFieldType(key = name)

                when {
                    assignedValueType != null -> UnorderedTupleMatch.PresentValueMatch(
                        valueMatch = valueType.match(
                            assignedType = assignedValueType as SpecificType,
                        )
                    )

                    else -> UnorderedTupleMatch.AbsentValueMismatch
                }
            },
        )

        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<SpecificType> =
        valueTypeByName.values.asSequence().flatMap { (it as SpecificType).walk() }

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> =
        TypePlaceholderSubstitution.traverseIterable(valueTypeByName.entries) { (name, type) ->
            type.substituteTypePlaceholders(
                resolution = resolution,
            ).transform { substitutedType ->
                name to substitutedType
            }
        }.transform { substitutedEntries ->
            UnorderedTupleType(
                valueTypeByName = substitutedEntries.toMap(),
            )
        }

    override fun isNonEquivalentToDirectly(
        innerContext: NonEquivalenceContext,
        otherType: SpecificType,
    ): Boolean {
        if (otherType !is UnorderedTupleType) return true

        if (keys != otherType.keys) return true

        return keys.any { key ->
            val thisValueType = getValueType(name = key) ?: return true
            val otherValueType = otherType.getValueType(name = key) ?: return true

            (thisValueType as SpecificType).isNonEquivalentToRecursively(
                outerContext = innerContext,
                otherType = otherValueType as SpecificType,
            )
        }
    }

    override fun buildVariableExpressionDirectly(
        context: VariableExpressionBuildingContext,
    ): Expression = UnorderedTupleTypeConstructor(
        entries = lazy {
            entries.mapNotNull { entry ->
                (entry.type as Type).buildVariableExpression(context = context)?.let { expression ->
                    UnorderedTupleTypeConstructor.Entry(
                        name = entry.name,
                        typeLazy = lazy { expression },
                    )
                }
            }.toSet()
        }
    )

    override fun buildTypeVariableDefinitions(): Set<TypeVariableDefinition> = valueTypeByName.mapNotNull { (_, type) ->
        if (type is TypeType) {
            TypeVariableDefinition()
        } else null
    }.toSet()
}

fun UnorderedTupleType(
    valueTypeByName: Map<Symbol, TypeAlike>,
): UnorderedTupleType = object : UnorderedTupleType() {
    override val valueTypeThunkByName = valueTypeByName.mapValues { (_, type) ->
        Thunk.pure(type)
    }
}
