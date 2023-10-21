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

sealed class Type : TypeAlike()

// Idea: "Direct" and "indirect" types?
// Idea: "Generic" and "Specific" types?
data class GenericType(
    val metaArgumentType: TupleType,
) : Type() {
    val metaType: UniversalFunctionType
        get() = UniversalFunctionType(
            argumentType = metaArgumentType,
            imageType = TypeType,
        )

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun dumpDirectly(depth: Int): String = "${metaArgumentType.dumpRecursively(depth)} !-> Type"
}
