package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

data class TypePlaceholder(
    val typeVariable: TypeVariable,
) : TypeAlike() {
    override fun resolveTypePlaceholders(
        assignedType: MembershipType,
    ): TypePlaceholderResolution = TypePlaceholderResolution(
        resolvedTypeByPlaceholder = mapOf(this to assignedType),
    )

    // Thought: Return an error if resolution misses this variable?
    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> {
        val resolvedType = resolution.resolvedTypeByPlaceholder[this]

        return if (resolvedType != null) {
            TypePlaceholderSubstitution(
                result = resolvedType,
            )
        } else {
            TypePlaceholderSubstitution(
                result = IllType,
                unresolvedPlaceholders = setOf(this),
            )
        }
    }

    override fun dumpDirectly(depth: Int): String = "$${typeVariable.dump()}"
}
