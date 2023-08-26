package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface Locatable {
    val location: SourceLocation
}

interface Dumpable {
    fun dump(): String
}

sealed interface ExpressionTerm : Locatable, Dumpable
