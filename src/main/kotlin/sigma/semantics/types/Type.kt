package sigma.semantics.types

import sigma.evaluation.values.IntValue
import sigma.evaluation.values.PrimitiveValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value

sealed class Type : Value() {
    sealed class MatchResult {
        abstract fun isFull(): Boolean

        abstract fun dump(): String
    }

    sealed class Match : MatchResult()

    sealed class PartialMatch : Match()

    object TotalMatch : Match() {
        override fun isFull(): Boolean = true

        override fun dump(): String = "(total match)"

        override fun toString(): String = "TotalMatch"
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

    final override fun toString(): String = dump()

    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

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
    ): MatchResult
}

object MetaType : Type() {
    override fun findLowestCommonSupertype(other: Type): Type = AnyType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = this

    override fun match(assignedType: Type): MatchResult = when (assignedType) {
          is MetaType -> TotalMatch
          else -> TotalMismatch(
              expectedType = MetaType,
              actualType = assignedType,
          )
      }

    override fun dump(): String = "(meta-type)"

}

object AnyType : Type() {
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
    ): MatchResult = Type.TotalMatch

    override fun dump(): String = "Any"
}

sealed interface PrimitiveLiteralType {
    val asType: PrimitiveType

    val value: PrimitiveValue
}

// TODO: Extract
sealed class PrimitiveType : Type() {
    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): PrimitiveType = this

    override fun match(
        assignedType: Type,
    ): MatchResult = when (this.findLowestCommonSupertype(assignedType)) {
        this -> TotalMatch
        else -> TotalMismatch(
            expectedType = this,
            actualType = assignedType,
        )
    }
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

object NeverType : Type() {
    object AssignmentMismatch : Type.Mismatch() {
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
    ): MatchResult = AssignmentMismatch

    override fun dump(): String = "Never"
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

    override val asType: PrimitiveType = this
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

    override val asType: PrimitiveType = this
}

/**
 * A symbol for an illegal type, a result of a typing error.
 */
object IllType : Type() {
    override fun findLowestCommonSupertype(other: Type): Type = IllType

    override fun resolveTypeVariables(assignedType: Type): TypeVariableResolution {
        // Note: This might need an improvement
        return TypeVariableResolution.Empty
    }

    override fun substituteTypeVariables(resolution: TypeVariableResolution): Type = IllType
    override fun match(assignedType: Type): MatchResult {
        TODO("Not yet implemented")
    }

    override fun dump(): String = "IllType"
}
