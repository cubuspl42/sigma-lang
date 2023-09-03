package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

data class TypeValue<TypeType : Type>(
    val asType: TypeType,
) : SealedValue() {
    override fun dump(): String = "(type)"
}

val Value.asType: Type?
    get() = (this as? TypeValue<*>)?.asType