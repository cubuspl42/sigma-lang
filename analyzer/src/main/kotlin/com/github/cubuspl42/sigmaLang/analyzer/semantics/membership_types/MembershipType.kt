package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue

sealed class MembershipType {
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
        val actualType: MembershipType,
    ) : Mismatch() {
        override fun dump(): String = "expected ${expectedType.dump()}, actual: ${actualType.dump()}"
    }


    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

    abstract fun dump(): String

    abstract fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType

    abstract fun resolveTypeVariables(
        assignedType: MembershipType,
    ): TypeVariableResolution

    abstract fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): MembershipType

    abstract fun match(
        assignedType: MembershipType,
    ): MembershipType.MatchResult

    // Thought: Wrong name? `walkChildren`?
    abstract fun walkRecursive(): Sequence<MembershipType>

    final override fun toString(): String = dump()
}

fun MembershipType.walk(): Sequence<MembershipType> = sequenceOf(this) + walkRecursive()

val <TypeType : MembershipType> TypeType.asValue: TypeValue<TypeType>
    get() = TypeValue(this)
