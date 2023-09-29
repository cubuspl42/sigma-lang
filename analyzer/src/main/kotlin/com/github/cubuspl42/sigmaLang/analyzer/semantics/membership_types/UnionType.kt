package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

data class UnionType(
    val memberTypes: Set<MembershipType>,
) : MembershipType() {
    data class UnionMatch(
        val expectedType: UnionType,
        val unmatchedTypes: Set<MembershipType>,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${unmatchedTypes.joinToString { it.dump() }} didn't match any of ${expectedType.dump()}"
    }

    data class AssignedUnionMatch(
        val expectedType: ShapeType,
        val nonMatchingTypes: Set<MembershipType>,
    ) : MembershipType.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${nonMatchingTypes.joinToString { it.dump() }} didn't match type ${expectedType.dump()}"
    }

    override fun dumpDirectly(depth: Int): String = memberTypes.joinToString(separator = " | ") { it.dumpRecursively(depth = depth + 1) }

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = UnionType(
        memberTypes + other, // This can actually work for any type; `findLowestCommonSupertype` should be re-thought
    )

    override fun resolveTypeVariables(
        assignedType: MembershipType,
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
        assignedType: MembershipType,
    ): MembershipType.MatchResult {
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
            MembershipType.TotalMatch
        }
    }

    private fun isAssignable(
        /**
         * Non-union type
         */
        assignedType: MembershipType,
    ): Boolean = memberTypes.any { memberType ->
        memberType.match(assignedType = assignedType).isFull()
    }

    override fun walkRecursive(): Sequence<MembershipType> = memberTypes.asSequence().flatMap {
        it.walk()
    }
}
