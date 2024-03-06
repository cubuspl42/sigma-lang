package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.PrimitiveValue

abstract class PrimitiveLiteralTerm : ExpressionTerm {
    abstract val value: PrimitiveValue
}
