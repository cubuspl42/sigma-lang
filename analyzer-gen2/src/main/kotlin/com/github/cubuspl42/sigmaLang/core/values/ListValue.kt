package com.github.cubuspl42.sigmaLang.core.values

data class ListValue(
    private val values: List<Value>,
) : Value()
