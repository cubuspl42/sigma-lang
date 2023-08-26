package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation

interface ExpressionTerm {
    val location: SourceLocation
}
