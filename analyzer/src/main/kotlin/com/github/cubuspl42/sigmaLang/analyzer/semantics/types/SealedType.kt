package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*

sealed class SealedType : SealedValue(), Type {
    final override val asSealed: SealedType
        get() = this

    final override val asValue: Value
        get() = this

    override val asValueThunk: Thunk<Type>
        get() = this.asThunk

    final override fun toString(): String = dump()

    override val asLiteral: PrimitiveLiteralType? = null

    override val asArray: ArrayType? = null

    override fun walk(): Sequence<Type> = sequenceOf(this) + walkRecursive()

}

object MetaType : SealedType() {
    override fun findLowestCommonSupertype(other: Type): Type = AnyType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = this

    override fun match(assignedType: Type): Type.MatchResult = when (assignedType) {
        is MetaType -> Type.TotalMatch
        else -> Type.TotalMismatch(
            expectedType = MetaType,
            actualType = assignedType,
        )
    }

    override fun walkRecursive(): Sequence<Type> = emptySequence()

    override fun dump(): String = "(meta-type)"

}

object AnyType : SealedType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = AnyType

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): AnyType = this

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = Type.TotalMatch

    override fun dump(): String = "Any"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}

sealed interface PrimitiveLiteralType {
    val asPrimitiveType: PrimitiveType

    val value: PrimitiveValue
}

// TODO: Extract
sealed class PrimitiveType : SealedType() {
    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): PrimitiveType = this

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = when (this.findLowestCommonSupertype(assignedType)) {
        this -> Type.TotalMatch
        else -> Type.TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }

    final override fun walkRecursive(): Sequence<Type> = emptySequence()

}

object UndefinedType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is UndefinedType -> UndefinedType
        else -> AnyType
    }

    override fun dump(): String = "Undefined"
}

object NeverType : SealedType() {
    data object AssignmentMismatch : Type.Mismatch() {
        override fun dump(): String = "nothing can be assigned to Never"
    }

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = other

    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type {
        return this
    }

    override fun match(
        assignedType: Type,
    ): Type.MatchResult = AssignmentMismatch

    override fun dump(): String = "Never"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}

object BoolType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ) = when (other) {
        is BoolType -> BoolType
        else -> AnyType
    }

    override fun dump(): String = "Bool"
}

sealed class IntType : PrimitiveType()

object IntCollectiveType : IntType() {
    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dump(): String = "Int"
}

data class IntLiteralType(
    override val value: IntValue,
) : IntType(), PrimitiveLiteralType {
    companion object {
        fun of(
            value: Long,
        ): IntLiteralType = IntLiteralType(
            value = IntValue(value = value),
        )
    }

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        this -> this
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dump(): String = "${value.value}"

    override val asLiteral = this

    override val asPrimitiveType: PrimitiveType = this
}

data class SymbolType(
    override val value: Symbol,
) : PrimitiveType(), PrimitiveLiteralType {
    companion object {
        fun of(
            name: String,
        ): SymbolType = SymbolType(
            value = Symbol.of(name = name),
        )
    }

    override fun dump(): String = value.dump()

    override val asLiteral = this

    override fun findLowestCommonSupertype(
        other: Type,
    ): Type = when (other) {
        this -> this
        else -> AnyType
    }

    override val asPrimitiveType: PrimitiveType = this
}

/**
 * A symbol for an illegal type, a result of a typing error.
 */
object IllType : SealedType() {
    override fun findLowestCommonSupertype(other: Type): Type = IllType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        // Note: This might need an improvement
        return TypeVariableResolution.Empty
    }

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = IllType
    override fun match(assignedType: Type): Type.MatchResult = Type.TotalMatch

    override fun dump(): String = "IllType"

    override fun walkRecursive(): Sequence<Type> = emptySequence()
}
