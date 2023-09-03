package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

sealed class Value {
    abstract fun dump(): String
}
