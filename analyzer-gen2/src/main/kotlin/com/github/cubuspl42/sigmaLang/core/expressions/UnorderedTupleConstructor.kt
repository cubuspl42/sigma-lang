package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.Identifier

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<Expression>>,
): Expression() {

}
