package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

// Thought: KindAlike?
abstract class TypeAlike {
    abstract fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution

    abstract fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    open fun match(
        assignedType: SpecificType,
    ): SpecificType.MatchResult {
        throw UnsupportedOperationException() // TODO: Only membership types should be able to match
    }

    fun dump(): String = dumpRecursively(depth = 0)

    fun dumpRecursively(depth: Int): String {
        if (depth > SpecificType.maxDumpDepth) return "(...)"

        return dumpDirectly(depth = depth)
    }

    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

    abstract fun dumpDirectly(depth: Int): String
}
