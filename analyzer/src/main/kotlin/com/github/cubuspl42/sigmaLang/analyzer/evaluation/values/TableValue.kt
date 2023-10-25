package com.github.cubuspl42.sigmaLang.analyzer.evaluation.values

sealed class TableValue : FunctionValue() {
    final override fun apply(
        argument: Value,
    ): Thunk<Value> = read(
        key = argument as PrimitiveValue,
    ) ?: Thunk.pure(
        UndefinedValue.withName(
            name = argument,
        )
    )

    abstract fun read(key: PrimitiveValue): Thunk<Value>?
}
