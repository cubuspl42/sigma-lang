package com.github.cubuspl42.sigmaLang.analyzer.semantics

interface ProjectStore {
    fun load(modulePath: ModulePath): String?
}
