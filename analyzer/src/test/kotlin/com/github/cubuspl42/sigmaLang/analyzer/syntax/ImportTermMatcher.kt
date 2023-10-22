package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath
import utils.Matcher

class ImportTermMatcher(
    private val modulePath: Matcher<ModulePath>
) : Matcher<ImportTerm>() {
    override fun match(actual: ImportTerm) {
        modulePath.match(actual = actual.modulePath)
    }
}
