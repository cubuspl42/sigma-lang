package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object BoolType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ) = when (other) {
        is BoolType -> BoolType
        else -> AnyType
    }

    override fun dump(): String = "Bool"
}