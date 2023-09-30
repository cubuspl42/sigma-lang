package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

/// Type of types
object TypeType : ShapeType() {
    override fun findLowestCommonSupertype(other: MembershipType): MembershipType = AnyType

    override fun resolveTypeVariablesShape(assignedType: MembershipType): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(resolution: TypeVariableResolution): MembershipType = this

    override fun matchShape(assignedType: MembershipType): MembershipType.MatchResult = when (assignedType) {
        is TypeType -> MembershipType.TotalMatch
        else -> MembershipType.TotalMismatch(
            expectedType = TypeType,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<MembershipType> = emptySequence()

    override fun dumpDirectly(depth: Int): String = "(meta-type)"
}
