package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

data class UnionType(
    val memberTypes: Set<Type>,
) : Type() {
    data class UnionMatch(
        val expectedType: UnionType,
        val unmatchedTypes: Set<Type>,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${unmatchedTypes.joinToString { it.dump() }} didn't match any of ${expectedType.dump()}"
    }

    data class AssignedUnionMatch(
        val expectedType: ShapeType,
        val nonMatchingTypes: Set<Type>,
    ) : Type.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${nonMatchingTypes.joinToString { it.dump() }} didn't match type ${expectedType.dump()}"
    }

    override fun dump(): String = memberTypes.joinToString(separator = " | ") { it.dump() }

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = UnionType(
        memberTypes + other, // This can actually work for any type; `findLowestCommonSupertype` should be re-thought
    )

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): UnionType = UnionType(
        memberTypes = memberTypes.map {
            it.substituteTypeVariables(
                resolution = resolution,
            )
        }.toSet()
    )

    override fun match(
        assignedType: Type,
    ): Type.MatchResult {
        val assignedMemberTypes = if (assignedType is UnionType) {
            assignedType.memberTypes
        } else {
            setOf(assignedType)
        }

        val unmatchedTypes = assignedMemberTypes.filterNot { assignedMemberType ->
            isAssignable(assignedType = assignedMemberType)
        }.toSet()

        return if (unmatchedTypes.isNotEmpty()) {
            UnionMatch(
                expectedType = this,
                unmatchedTypes = unmatchedTypes,
            )
        } else {
            Type.TotalMatch
        }
    }

    private fun isAssignable(
        /**
         * Non-union type
         */
        assignedType: Type,
    ): Boolean = memberTypes.any { memberType ->
        memberType.match(assignedType = assignedType).isFull()
    }

    override fun walkRecursive(): Sequence<Type> = memberTypes.asSequence().flatMap {
        it.walk()
    }
}
