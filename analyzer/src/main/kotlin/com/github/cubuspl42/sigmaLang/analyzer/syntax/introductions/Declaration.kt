package com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type

interface Declaration {
    val declaredType: Type
}
