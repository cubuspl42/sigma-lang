package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Abstraction
import org.antlr.v4.runtime.Token
import java.lang.IllegalArgumentException

enum class Constness {
    Const, Variable;

    companion object {
        fun build(ctx: Token): Constness = when (ctx.text) {
            "^" -> Constness.Variable
            "!" -> Constness.Const
            else -> throw IllegalArgumentException()
        }
    }
}

// Type of tables with fixed number of entries, with keys being symbols, and any
// values
data class UnorderedTupleType(
    override val constness: Constness = Constness.Variable,
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

    override fun resolveTypeVariablesShape(assignedType: Type): TypeVariableResolution {
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
        assignedType: Type,
    ): Type.MatchResult = when (assignedType) {
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

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = valueTypeByName.values.asSequence().flatMap { it.walk() }

    override fun toArgumentDeclarationBlock(): Abstraction.ArgumentStaticBlock = Abstraction.ArgumentStaticBlock(
        argumentDeclarations = valueTypeByName.map { (name, type) ->
            Abstraction.ArgumentDeclaration(
                name = name,
                annotatedType = type,
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

    override fun toArgumentScope(argument: DictValue): DynamicScope = object : DynamicScope {
        override fun getValue(
            name: Symbol,
        ): Thunk<Value>? = argument.read(name)?.toThunk()
    }
}
