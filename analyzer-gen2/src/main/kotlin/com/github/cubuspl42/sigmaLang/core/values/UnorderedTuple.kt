package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.Identifier

class UnorderedTuple(
    private val valueByKey: Map<Identifier, Lazy<Value>>,
) : Value()
