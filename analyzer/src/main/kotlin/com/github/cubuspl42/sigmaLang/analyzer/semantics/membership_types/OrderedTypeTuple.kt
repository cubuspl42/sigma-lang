package com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types

data class OrderedTypeTuple(
    val elements: List<TypeEntity>,
) : TypeEntity()
