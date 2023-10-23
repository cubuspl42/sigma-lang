package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

interface Declaration : Introduction {
    val annotatedType: Type
}
