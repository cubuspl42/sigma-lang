package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike

data class TypeValue<TypeType : TypeAlike>(
    val asType: TypeType,
) : Value() {
    override fun dump(): String = "(type)"
}

val Value.asType: TypeAlike?
    get() = (this as? TypeValue<*>)?.asType
