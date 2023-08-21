package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: Type,
    override val valueType: Type,
) : TableType() {
    data class DictMatch(
        val keyMatch: Type.MatchResult,
        val valueMatch: Type.MatchResult,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = keyMatch.isFull() && valueMatch.isFull()
        override fun dump(): String = when {
            !keyMatch.isFull() -> "key: " + keyMatch.dump()
            !valueMatch.isFull() -> "value: " + valueMatch.dump()
            else -> "(?)"
        }
    }

    override fun dump(): String = "{${keyType.dump()} ~> ${valueType.dump()}}"

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution {
        if (assignedType !is DictType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-dict is assigned",
        )

        val keyResolution = keyType.resolveTypeVariables(
            assignedType = assignedType.keyType,
        )

        val valueResolution = valueType.resolveTypeVariables(
            assignedType = assignedType.valueType,
        )

        return keyResolution.mergeWith(valueResolution)
    }

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): DictType = DictType(
        keyType = keyType.substituteTypeVariables(
            resolution = resolution,
        ),
        valueType = valueType.substituteTypeVariables(
            resolution = resolution,
        ),
    )

    override fun match(
        assignedType: Type,
    ): MatchResult = when (assignedType) {
        is DictType -> DictMatch(
            keyMatch = assignedType.keyType.match(
                assignedType = keyType,
            ),
            valueMatch = valueType.match(
                assignedType = assignedType.valueType,
            ),
        )

        is UnorderedTupleType -> when {
            assignedType.isDefinitelyEmpty() -> TotalMatch
            else -> Type.TotalMismatch(
                expectedType = this,
                actualType = assignedType,
            )
        }

        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = sequence {
        yieldAll(keyType.walk())
        yieldAll(valueType.walk())
    }
}