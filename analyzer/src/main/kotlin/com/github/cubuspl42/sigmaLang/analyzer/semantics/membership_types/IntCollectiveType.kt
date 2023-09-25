package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object IntCollectiveType : IntType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = when (other) {
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dump(): String = "Int"
}
