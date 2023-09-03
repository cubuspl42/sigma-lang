package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value

interface Type {
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
        val expectedType: Type,
        val actualType: Type,
    ) : Mismatch() {
        override fun dump(): String = "expected ${expectedType.dump()}, actual: ${actualType.dump()}"
    }

    val asLiteral: PrimitiveLiteralType?

    val asArray: ArrayType?

    fun dump(): String

    fun findLowestCommonSupertype(
        other: Type,
    ): Type

    fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution

    fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type

    fun match(
        assignedType: Type,
    ): Type.MatchResult

    fun walk(): Sequence<Type>

    fun walkRecursive(): Sequence<Type>
}
