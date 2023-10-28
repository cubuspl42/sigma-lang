package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

abstract class FunctionValue : Value() {
    fun toList(): List<Value> = generateSequence(0) { it + 1 }.map {
        apply(
            argument = IntValue(value = it.toLong()),
        ).evaluateInitialValue()
    }.takeWhile { it !is UndefinedValue }.toList()

    abstract fun apply(
        argument: Value,
    ): Thunk<Value>

    fun applyOrdered(
        vararg arguments: Value,
    ): Thunk<Value> = apply(
        argument = DictValue.fromList(arguments.toList()),
    )
}
