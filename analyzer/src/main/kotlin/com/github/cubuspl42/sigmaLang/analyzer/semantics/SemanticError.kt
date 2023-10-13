package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface SemanticError {
    fun dump(): String = toString()

    val location: SourceLocation?
        get() = null
}
