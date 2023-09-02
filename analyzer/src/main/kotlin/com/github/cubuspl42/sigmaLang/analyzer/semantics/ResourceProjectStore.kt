package com.github.cubuspl42.sigmaLang.analyzer.semantics

class ResourceProjectStore(private val javaClass: Class<*>) : ProjectStore {
    override fun load(modulePath: ModulePath): String {
        val fileName = "${modulePath.name}.sigma"
        val content = javaClass.getResource(fileName)?.readText()
        return content ?: throw RuntimeException("Couldn't load the source file: $fileName")
    }
}
