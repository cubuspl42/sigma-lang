package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

class ProviderAbstraction(
    private val result: Value,
) : Abstraction() {
    override fun apply(
        argument: Value,
    ): Thunk<Value> = Thunk.pure(result)
}
