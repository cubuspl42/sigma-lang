package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

abstract class Symbol : PrimitiveValue() {
    companion object {
        fun of(
            name: String,
        ): Symbol = Identifier.of(name = name)
    }
}
