package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

// Thought: Is this needed anymore?
abstract class ComputableFunctionValue : FunctionValue() {

    override fun dump(): String = "(computable function)"
}
