package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

data class SymbolType(
    override val value: Identifier,
) : PrimitiveType(), PrimitiveLiteralType {
    companion object {
        fun of(
            name: String,
        ): SymbolType = SymbolType(
            value = Identifier.of(name = name),
        )
    }

    override fun dumpDirectly(depth: Int): String = value.dump()

    override val asLiteral = this

    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = when (other) {
        this -> this
        else -> AnyType
    }

    override val asPrimitiveType: PrimitiveType = this
}
