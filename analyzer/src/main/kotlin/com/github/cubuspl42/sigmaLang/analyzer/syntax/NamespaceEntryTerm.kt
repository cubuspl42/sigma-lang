package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

sealed interface NamespaceEntryTerm {
    val name: Symbol
}
