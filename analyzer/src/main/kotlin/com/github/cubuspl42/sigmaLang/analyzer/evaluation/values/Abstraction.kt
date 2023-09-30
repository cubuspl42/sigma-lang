package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

abstract class Abstraction : ComputableFunctionValue() {
    override fun dump(): String = "(abstraction)"

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
