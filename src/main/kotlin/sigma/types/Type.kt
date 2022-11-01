package sigma.types

import sigma.values.IntValue
import sigma.values.PrimitiveValue
import sigma.values.Symbol
import sigma.values.Value

sealed class Type {
    final override fun toString(): String = dump()

    open val asLiteral: LiteralType? = null

    abstract fun isAssignableTo(otherType: Type): Boolean

    abstract fun dump(): String
}

sealed interface LiteralType {
    val value: PrimitiveValue
}

object UndefinedType : Type() {
    override fun isAssignableTo(
        otherType: Type,
    ): Boolean = otherType is UndefinedType

    override fun dump(): String = "Undefined"
}

sealed class PrimitiveType : Type()

object BoolType : PrimitiveType() {
    override fun isAssignableTo(
        otherType: Type,
    ): Boolean = otherType is BoolType

    override fun dump(): String = "Bool"
}

sealed class IntType : PrimitiveType()

object IntCollectiveType : IntType() {
    override fun isAssignableTo(
        otherType: Type,
    ): Boolean = otherType is IntCollectiveType

    override fun dump(): String = "Int"
}

data class IntLiteralType(
    override val value: IntValue,
) : IntType(), LiteralType {
    companion object {
        fun of(
            value: Int,
        ): IntLiteralType = IntLiteralType(
            value = IntValue(value = value),
        )
    }

    override fun dump(): String = value.toString()

    override val asLiteral = this

    override fun isAssignableTo(
        otherType: Type,
    ): Boolean = otherType == this || otherType is IntCollectiveType
}

data class SymbolType(
    override val value: Symbol,
) : PrimitiveType(), LiteralType {
    companion object {
        fun of(
            name: String,
        ): SymbolType = SymbolType(
            value = Symbol.of(name = name),
        )
    }

    override fun dump(): String = value.dump()

    override val asLiteral = this

    override fun isAssignableTo(
        otherType: Type,
    ): Boolean = this == otherType
}
