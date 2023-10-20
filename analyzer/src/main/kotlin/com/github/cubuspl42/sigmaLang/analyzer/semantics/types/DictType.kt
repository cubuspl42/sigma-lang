package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: TypeAlike,
    override val valueType: TypeAlike,
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

    override fun dumpDirectly(depth: Int): String =
        "{${keyType.dump()} ~> ${valueType.dumpRecursively(depth = depth + 1)}}"

    override fun isDefinitelyEmpty(): Boolean = false

    override fun resolveTypeVariablesShape(
        assignedType: TypeAlike,
    ): TypePlaceholderResolution {
        if (assignedType !is DictType) throw TypeErrorException(
            message = "Cannot resolve type variables, non-dict is assigned",
        )

        val keyResolution = keyType.resolveTypePlaceholders(
            assignedType = assignedType.keyType as MembershipType,
        )

        val valueResolution = valueType.resolveTypePlaceholders(
            assignedType = assignedType.valueType as MembershipType,
        )

        return keyResolution.mergeWith(valueResolution)
    }

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.combine2(
        substitutionA = keyType.substituteTypePlaceholders(
            resolution = resolution,
        ),
        substitutionB = valueType.substituteTypePlaceholders(
            resolution = resolution,
        ),
    ) { keyType, valueType ->
        DictType(
            keyType = keyType as TypeAlike,
            valueType = valueType as TypeAlike,
        )
    }

    override fun matchShape(
        assignedType: MembershipType,
    ): MembershipType.MatchResult = when (val sealedAssignedType = assignedType) {
        is DictType -> DictMatch(
            keyMatch = sealedAssignedType.keyType.match(
                assignedType = keyType as MembershipType,
            ),
            valueMatch = valueType.match(
                assignedType = sealedAssignedType.valueType as MembershipType,
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
        yieldAll((keyType as MembershipType).walk())
        yieldAll((valueType as MembershipType).walk())
    }
}
