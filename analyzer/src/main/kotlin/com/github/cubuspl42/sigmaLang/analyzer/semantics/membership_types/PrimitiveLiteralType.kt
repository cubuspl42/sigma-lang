package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue

sealed interface PrimitiveLiteralType {
    val asPrimitiveType: PrimitiveType

    val value: PrimitiveValue
}