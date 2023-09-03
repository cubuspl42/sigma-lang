package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

data class SetValue(
    val elements: Set<Value>,
) : SealedValue() {
    override fun dump(): String = "{${elements.joinToString(separator = ", ") { it.dump() }}}"
}
