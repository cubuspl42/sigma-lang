package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

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
        other: SpecificType,
    ): SpecificType = when (other) {
        this -> this
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "${value.value}"

    override val asLiteral = this

    override val asPrimitiveType: PrimitiveType = this
}
