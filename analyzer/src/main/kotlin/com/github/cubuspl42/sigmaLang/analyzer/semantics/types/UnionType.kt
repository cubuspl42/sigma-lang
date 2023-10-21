package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

data class UnionType(
    val memberTypes: Set<TypeAlike>,
) : SpecificType() {
    data class UnionMatch(
        val expectedType: UnionType,
        val unmatchedTypes: Set<SpecificType>,
    ) : SpecificType.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${unmatchedTypes.joinToString { it.dump() }} didn't match any of ${expectedType.dump()}"
    }

    data class AssignedUnionMatch(
        val expectedType: ShapeType,
        val nonMatchingTypes: Set<SpecificType>,
    ) : SpecificType.PartialMatch() {
        override fun isFull(): Boolean = false

        override fun dump(): String =
            "union members ${nonMatchingTypes.joinToString { it.dump() }} didn't match type ${expectedType.dump()}"
    }

    override fun dumpDirectly(depth: Int): String =
        memberTypes.joinToString(separator = " | ") { it.dumpRecursively(depth = depth + 1) }

    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = UnionType(
        memberTypes + other, // This can actually work for any type; `findLowestCommonSupertype` should be re-thought
    )

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution.traverseIterable(memberTypes) {
        it.substituteTypePlaceholders(
            resolution = resolution,
        )
    }.transform { substitutedMemberTypes ->
        UnionType(
            memberTypes = substitutedMemberTypes.toSet(),
        )
    }

    override fun match(
        assignedType: SpecificType,
    ): SpecificType.MatchResult {
        val assignedMemberTypes = if (assignedType is UnionType) {
            assignedType.memberTypes
        } else {
            setOf(assignedType)
        }

        val unmatchedTypes = assignedMemberTypes.mapNotNull { assignedMemberType ->
            (assignedMemberType as SpecificType).takeIf { !isAssignable(assignedType = it) }
        }.toSet()

        return if (unmatchedTypes.isNotEmpty()) {
            UnionMatch(
                expectedType = this,
                unmatchedTypes = unmatchedTypes,
            )
        } else {
            SpecificType.TotalMatch
        }
    }

    private fun isAssignable(
        /**
         * Non-union type
         */
        assignedType: SpecificType,
    ): Boolean = memberTypes.any { memberType ->
        memberType.match(assignedType = assignedType).isFull()
    }

    override fun walkRecursive(): Sequence<SpecificType> = memberTypes.asSequence().flatMap {
        (it as SpecificType).walk()
    }
}
