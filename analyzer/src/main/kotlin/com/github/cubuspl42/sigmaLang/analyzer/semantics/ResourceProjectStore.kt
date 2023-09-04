package com.github.cubuspl42.sigmaLang.analyzer.semantics

class ResourceProjectStore(private val javaClass: Class<*>) : ProjectStore {
    override fun load(modulePath: ModulePath): String? {
        val fileName = "${modulePath.name}.sigma"
        return javaClass.getResource(fileName)?.readText()
    }
}
