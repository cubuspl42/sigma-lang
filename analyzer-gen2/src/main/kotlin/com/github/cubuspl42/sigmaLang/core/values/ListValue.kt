package com.github.cubuspl42.sigmaLang.core.values

data class ListValue(
    private val list: List<Value>,
) : Value()
