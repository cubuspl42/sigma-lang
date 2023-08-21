package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

data class OrderedTypeTuple(
    val elements: List<TypeEntity>,
) : TypeEntity()
