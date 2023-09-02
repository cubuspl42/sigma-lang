package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.semantics.ModulePath

interface ImportTerm {
    val modulePath: ModulePath
}
