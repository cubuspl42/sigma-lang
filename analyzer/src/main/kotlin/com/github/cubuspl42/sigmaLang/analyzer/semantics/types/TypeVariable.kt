package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

class TypeVariable() : SpecificType() {
    fun toPlaceholder(): TypePlaceholder = TypePlaceholder(
        typeVariable = this,
    )

    override fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType = AnyType

    override fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution = TypePlaceholderResolution.Empty

    // TODO: Extract a class for "simple" types?
    override fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike> = TypePlaceholderSubstitution(
        result = this,
    )

    override fun match(assignedType: SpecificType): SpecificType.MatchResult = when (assignedType) {
        this -> SpecificType.TotalMatch
        else -> SpecificType.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    override fun isNonEquivalentToDirectly(
        innerContext: NonEquivalenceContext,
        otherType: SpecificType,
    ): Boolean = this !== otherType

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun dumpDirectly(depth: Int): String = "TV#${System.identityHashCode(this).toString(16)}"
}

data class TypePlaceholderResolution(
    val resolvedTypeByPlaceholder: Map<TypePlaceholder, SpecificType>,
) {
    val resolvedTypeVariables: Set<TypePlaceholder>
        get() = resolvedTypeByPlaceholder.keys

    fun mergeWith(
        other: TypePlaceholderResolution,
    ): TypePlaceholderResolution {
        // TODO: Check for resolution incompatibilities
        return TypePlaceholderResolution(
            resolvedTypeByPlaceholder = resolvedTypeByPlaceholder + other.resolvedTypeByPlaceholder,
        )
    }

    companion object {
        val Empty = TypePlaceholderResolution(
            resolvedTypeByPlaceholder = emptyMap(),
        )
    }
}

data class TypePlaceholderSubstitution<ResultType>(
    val result: ResultType,
    val unresolvedPlaceholders: Set<TypePlaceholder> = emptySet(),
) {
    companion object {
        fun <A, B, Result> combine2(
            substitutionA: TypePlaceholderSubstitution<A>,
            substitutionB: TypePlaceholderSubstitution<B>,
            combine: (A, B) -> Result,
        ): TypePlaceholderSubstitution<Result> {
            val resolvedType = combine(substitutionA.result, substitutionB.result)

            val unresolvedPlaceholders = substitutionA.unresolvedPlaceholders + substitutionB.unresolvedPlaceholders

            return TypePlaceholderSubstitution(
                result = resolvedType,
                unresolvedPlaceholders = unresolvedPlaceholders,
            )
        }

        // traverseIterable
        fun <ElementType, ResultType> traverseIterable(
            substitutions: Iterable<ElementType>,
            extract: (ElementType) -> TypePlaceholderSubstitution<ResultType>,
        ): TypePlaceholderSubstitution<List<ResultType>> {
            val unresolvedPlaceholders = mutableSetOf<TypePlaceholder>()

            val results = substitutions.map { substitution ->
                val extracted = extract(substitution)
                unresolvedPlaceholders.addAll(extracted.unresolvedPlaceholders)
                extracted.result
            }

            return TypePlaceholderSubstitution(
                result = results,
                unresolvedPlaceholders = unresolvedPlaceholders,
            )
        }
    }

    fun <ResultBType> transform(
        transform: (ResultType) -> ResultBType,
    ): TypePlaceholderSubstitution<ResultBType> = TypePlaceholderSubstitution(
        result = transform(result),
        unresolvedPlaceholders = unresolvedPlaceholders,
    )
}

data class TypeVariableResolutionError(
    override val message: String,
) : Exception()
