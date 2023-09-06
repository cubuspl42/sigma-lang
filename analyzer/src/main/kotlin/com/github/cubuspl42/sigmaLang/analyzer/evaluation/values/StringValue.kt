package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

data class StringValue(
    val value: String,
) : PrimitiveValue() {
    companion object {
        val Empty = StringValue("")
    }

    override fun dump(): String = "\"" + value + "\""
}
