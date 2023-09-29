package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: MembershipType,
    override val valueType: MembershipType,
) : TableType() {
    data class DictMatch(
        val keyMatch: MembershipType.MatchResult,
        val valueMatch: MembershipType.MatchResult,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = keyMatch.isFull() && valueMatch.isFull()
        override fun dump(): String = when {
            !keyMatch.isFull() -> "key: " + keyMatch.dump()
            !valueMatch.isFull() -> "value: " + valueMatch.dump()
            else -> "(?)"
        }
    }

    override fun dumpDirectly(depth: Int): String = "{${keyType.dump()} ~> ${valueType.dumpRecursively(depth = depth + 1)}}"

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariablesShape(
        assignedType: MembershipType,
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

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (val sealedAssignedType = assignedType) {
        is DictType -> DictMatch(
            keyMatch = sealedAssignedType.keyType.match(
                assignedType = keyType,
            ),
            valueMatch = valueType.match(
                assignedType = sealedAssignedType.valueType,
            ),
        )

        is UnorderedTupleType -> when {
            sealedAssignedType.isDefinitelyEmpty() -> MembershipType.TotalMatch
            else -> MembershipType.TotalMismatch(
                expectedType = this,
                actualType = sealedAssignedType,
            )
        }

        else -> MembershipType.TotalMismatch(
            expectedType = this,
            actualType = sealedAssignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = sequence {
        yieldAll(keyType.walk())
        yieldAll(valueType.walk())
    }
}
