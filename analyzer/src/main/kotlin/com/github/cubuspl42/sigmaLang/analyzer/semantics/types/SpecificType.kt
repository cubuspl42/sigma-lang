package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.utils.UnorderedPair

sealed class SpecificType : Type() {
    data class NonEquivalenceContext(
        val visitedPairs: Set<UnorderedPair<SpecificType>>,
    ) {
        fun withVisited(pair: UnorderedPair<SpecificType>): NonEquivalenceContext = NonEquivalenceContext(
            visitedPairs = visitedPairs + pair,
        )

        companion object {
            val Empty: NonEquivalenceContext = NonEquivalenceContext(
                visitedPairs = emptySet(),
            )
        }
    }

    sealed class MatchResult {
        abstract fun isFull(): Boolean

        abstract fun dump(): String
    }

    sealed class Match : MatchResult()

    sealed class PartialMatch : Match()

    data object TotalMatch : Match() {
        override fun isFull(): Boolean = true

        override fun dump(): String = "(total match)"
    }

    sealed class Mismatch : MatchResult() {
        final override fun isFull(): Boolean = false
    }

    data class TotalMismatch(
        val expectedType: SpecificType,
        val actualType: TypeAlike,
    ) : Mismatch() {
        override fun dump(): String = "expected ${expectedType.dump()}, actual: ${actualType.dump()}"
    }

    companion object {
        const val maxDumpDepth = 16
    }

    abstract fun findLowestCommonSupertype(
        other: SpecificType,
    ): SpecificType

    // Thought: Wrong name? `walkChildren`?
    abstract fun walkRecursive(): Sequence<SpecificType>

    fun isNonEquivalentTo(otherType: SpecificType): Boolean = isNonEquivalentToRecursively(
        outerContext = NonEquivalenceContext.Empty,
        otherType = otherType,
    )

    fun isNonEquivalentToRecursively(
        outerContext: NonEquivalenceContext,
        otherType: SpecificType,
    ): Boolean {
        val pair = UnorderedPair(this, otherType)

        if (pair in outerContext.visitedPairs) return false

        return isNonEquivalentToDirectly(
            innerContext = outerContext.withVisited(pair),
            otherType = otherType,
        )
    }

    open fun isNonEquivalentToDirectly(
        innerContext: NonEquivalenceContext,
        otherType: SpecificType,
    ): Boolean {
        // This implementation doesn't support cycles and should be removed eventually
        return this != otherType
    }

    fun isEquivalentTo(otherType: SpecificType): Boolean =
        !isNonEquivalentTo(otherType = otherType)

    override fun specifyImplicitly(): Type = this

    final override fun toString(): String = dump()
}

fun SpecificType.walk(): Sequence<SpecificType> = sequenceOf(this) + walkRecursive()

val <TypeType : TypeAlike> TypeType.asValue: TypeValue<TypeType>
    get() = TypeValue(this)
