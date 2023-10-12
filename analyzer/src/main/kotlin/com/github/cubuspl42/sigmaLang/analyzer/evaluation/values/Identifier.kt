package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

data class Identifier(
    val name: String,
) : PrimitiveValue() {
    companion object {
        fun of(
            name: String,
        ): Identifier = Identifier(
            name = name,
        )
    }

    override fun dump(): String = "`$name`"
}
