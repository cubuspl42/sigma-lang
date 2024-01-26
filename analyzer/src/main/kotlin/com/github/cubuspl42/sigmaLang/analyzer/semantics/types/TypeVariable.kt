package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.utils.MapUtils
import com.github.cubuspl42.sigmaLang.analyzer.utils.SetUtils

data class TypeVariable(
    val traitDeclaration: Declaration,
    val path: Path,
) : SpecificType() {
    data class Path(
        val root: Segment?,
    ) {
        data class Segment(
            val key: PrimitiveValue,
            val tail: Segment?,
        ) {
            companion object {
                fun of(key: PrimitiveValue): Segment = Segment(
                    key = key, tail = null
                )
            }

            fun extend(newKey: PrimitiveValue): Segment = Segment(
                key = key, tail = tail?.extend(key) ?: Segment.of(newKey)
            )

            fun dump(): String = listOfNotNull(
                key.dump(),
                tail?.dump(),
            ).joinToString(separator = ".")
        }

        companion object {
            fun of(
                vararg keys: PrimitiveValue,
            ): Path = keys.fold(
                initial = Root,
                operation = { path, key ->
                    path.extend(key)
                }
            )

            val Root = Path(root = null)
        }

        fun extend(
            newKey: PrimitiveValue,
        ): Path = Path(
            root = root?.extend(newKey) ?: Segment.of(newKey),
        )

        fun dump(): String = root?.dump() ?: "∅"
    }

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
    ): Boolean = this != otherType

    override fun walkRecursive(): Sequence<SpecificType> = emptySequence()

    override fun dumpDirectly(depth: Int): String =
        "#${System.identityHashCode(traitDeclaration).toString(16)}.${path.dump()}"
}

data class TypePlaceholderResolution(
    val resolvedTypeByPlaceholder: Map<TypePlaceholder, SpecificType>,
) {
    fun mergeWith(
        other: TypePlaceholderResolution,
    ): TypePlaceholderResolution = TypePlaceholderResolution(
        resolvedTypeByPlaceholder = MapUtils.merge(
            this.resolvedTypeByPlaceholder,
            other.resolvedTypeByPlaceholder,
        ) { e1: SpecificType, e2: SpecificType ->
            e1.findLowestCommonSupertype(e2)
        }
    )

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
