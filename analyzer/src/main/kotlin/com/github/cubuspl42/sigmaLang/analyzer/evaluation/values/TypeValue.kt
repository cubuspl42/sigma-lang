package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

data class TypeValue<TypeType : MembershipType>(
    val asType: TypeType,
) : Value() {
    override fun dump(): String = "(type)"
}

val Value.asType: MembershipType?
    get() = (this as? TypeValue<*>)?.asType
