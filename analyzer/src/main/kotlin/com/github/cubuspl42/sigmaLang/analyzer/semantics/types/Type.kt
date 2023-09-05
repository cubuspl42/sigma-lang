package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue

sealed class Type {
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


    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

    abstract fun dump(): String

    abstract fun findLowestCommonSupertype(
        other: Type,
    ): Type

    abstract fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution

    abstract fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type

    abstract fun match(
        assignedType: Type,
    ): Type.MatchResult

    // Thought: Wrong name? `walkChildren`?
    abstract fun walkRecursive(): Sequence<Type>

    final override fun toString(): String = dump()
}

fun Type.walk(): Sequence<Type> = sequenceOf(this) + walkRecursive()

val <TypeType : Type> TypeType.asValue: TypeValue<TypeType>
    get() = TypeValue(this)
