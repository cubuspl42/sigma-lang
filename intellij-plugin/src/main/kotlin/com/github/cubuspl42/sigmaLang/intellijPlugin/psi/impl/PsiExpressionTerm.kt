package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.Dumpable
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.Locatable

abstract class PsiExpressionTerm : Locatable, Dumpable {
    final override val location: SourceLocation
        get() = SourceLocation(lineIndex = -1, columnIndex = -1)

    final override fun dump(): String = toString()
}
