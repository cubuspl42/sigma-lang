package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

object UndefinedType : PrimitiveType() {
    override fun findLowestCommonSupertype(
        other: MembershipType,
    ): MembershipType = when (other) {
        is UndefinedType -> UndefinedType
        else -> AnyType
    }

    override fun dumpDirectly(depth: Int): String = "Undefined"
}
