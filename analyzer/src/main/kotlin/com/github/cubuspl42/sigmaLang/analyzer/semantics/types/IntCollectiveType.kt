package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

object IntCollectiveType : IntType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = when (other) {
        is IntType -> IntCollectiveType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "Int"
}
