package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.utils.UnorderedPair

abstract class TypeAlike {
    abstract fun resolveTypePlaceholders(
        assignedType: MembershipType,
    ): TypePlaceholderResolution

    abstract fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    open fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult {
        throw UnsupportedOperationException() // TODO: Only membership types should be able to match
    }

    fun dump(): String = dumpRecursively(depth = 0)

    fun dumpRecursively(depth: Int): String {
        if (depth > MembershipType.maxDumpDepth) return "(...)"

        return dumpDirectly(depth = depth)
    }

    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

    abstract fun dumpDirectly(depth: Int): String
}

sealed class MembershipType : TypeAlike() {
    data class NonEquivalenceContext(
        val visitedPairs: Set<UnorderedPair<MembershipType>>,
    ) {
        fun withVisited(pair: UnorderedPair<MembershipType>): NonEquivalenceContext = NonEquivalenceContext(
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
        val expectedType: MembershipType,
        val actualType: TypeAlike,
    ) : Mismatch() {
        override fun dump(): String = "expected ${expectedType.dump()}, actual: ${actualType.dump()}"
    }

    companion object {
        const val maxDumpDepth = 16
    }

    abstract fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType

    // Thought: Wrong name? `walkChildren`?
    abstract fun walkRecursive(): Sequence<MembershipType>

    fun isNonEquivalentTo(otherType: MembershipType): Boolean = isNonEquivalentToRecursively(
        outerContext = NonEquivalenceContext.Empty,
        otherType = otherType,
    )

    fun isNonEquivalentToRecursively(
        outerContext: NonEquivalenceContext,
        otherType: MembershipType,
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
        otherType: MembershipType,
    ): Boolean {
        // This implementation doesn't support cycles and should be removed eventually
        return this != otherType
    }

    fun isEquivalentTo(otherType: MembershipType): Boolean =
        !isNonEquivalentTo(otherType = otherType)

    final override fun toString(): String = dump()
}

fun MembershipType.walk(): Sequence<MembershipType> = sequenceOf(this) + walkRecursive()

val <TypeType : TypeAlike> TypeType.asValue: TypeValue<TypeType>
    get() = TypeValue(this)
