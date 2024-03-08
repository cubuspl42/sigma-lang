package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm

class ModuleLoader(
    @Suppress("PrivatePropertyName") private val class_: Class<*>,
) {
    fun loadModule(
        modulePath: ModulePath,
    ): ModuleTerm {
        val moduleFileRelativePath = "${modulePath.name.name}.sigma"

        val moduleUrl = class_.getResource(moduleFileRelativePath)
            ?: throw IllegalArgumentException("No source file found: $moduleFileRelativePath")

        val moduleSource = moduleUrl.readText()

        return ModuleTerm.parse(source = moduleSource)
    }
}
