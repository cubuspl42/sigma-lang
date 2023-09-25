package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue

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
        other: MembershipType,
    ): MembershipType = when (other) {
        this -> this
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dump(): String = "${value.value}"

    override val asLiteral = this

    override val asPrimitiveType: PrimitiveType = this
}
