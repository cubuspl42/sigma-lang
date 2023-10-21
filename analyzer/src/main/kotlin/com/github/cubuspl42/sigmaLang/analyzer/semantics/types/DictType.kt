package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeErrorException

// Type of tables with keys of a single primitive type and values of a single
// specific type
data class DictType(
    override val keyType: TypeAlike,
    override val valueType: TypeAlike,
) : TableType() {
    data class DictMatch(
        val keyMatch: SpecificType.MatchResult,
        val valueMatch: SpecificType.MatchResult,
    ) : SpecificType.PartialMatch() {
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
            assignedType = assignedType.keyType as SpecificType,
        )

        val valueResolution = valueType.resolveTypePlaceholders(
            assignedType = assignedType.valueType as SpecificType,
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
        assignedType: SpecificType,
    ): SpecificType.MatchResult = when (val sealedAssignedType = assignedType) {
        is DictType -> DictMatch(
            keyMatch = sealedAssignedType.keyType.match(
                assignedType = keyType as SpecificType,
            ),
            valueMatch = valueType.match(
                assignedType = sealedAssignedType.valueType as SpecificType,
            ),
        )

        is UnorderedTupleType -> when {
            sealedAssignedType.isDefinitelyEmpty() -> SpecificType.TotalMatch
            else -> SpecificType.TotalMismatch(
                expectedType = this,
                actualType = sealedAssignedType,
            )
        }

        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = sealedAssignedType,
        )
    }

    override fun walkRecursive(): Sequence<SpecificType> = sequence {
        yieldAll((keyType as SpecificType).walk())
        yieldAll((valueType as SpecificType).walk())
    }
}
