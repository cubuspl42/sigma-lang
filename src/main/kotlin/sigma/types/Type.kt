package sigma.types

import sigma.values.IntValue
import sigma.values.PrimitiveValue
import sigma.values.Symbol

sealed class Type {
    final override fun toString(): String = dump()

    open val asLiteral: PrimitiveLiteralType? = null

    abstract fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution

    abstract fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): Type

    abstract fun dump(): String
}

object AnyType : Type() {
    override fun resolveTypeVariables(
        assignedType: Type,
    ): TypeVariableResolution = TypeVariableResolution.Empty

    override fun substituteTypeVariables(
        resolution: TypeVariableResolution,
    ): AnyType = this

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
}

object UndefinedType : PrimitiveType() {
    override fun dump(): String = "Undefined"
}

object NeverType : PrimitiveType() {
    override fun dump(): String = "Never"
}

object BoolType : PrimitiveType() {
    override fun dump(): String = "Bool"
}

sealed class IntType : PrimitiveType()

object IntCollectiveType : IntType() {
    override fun dump(): String = "Int"
}

data class IntLiteralType(
    override val value: IntValue,
) : IntType(), PrimitiveLiteralType {
    companion object {
        fun of(
            value: Int,
        ): IntLiteralType = IntLiteralType(
            value = IntValue(value = value),
        )
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

    override val asType: PrimitiveType = this
}
