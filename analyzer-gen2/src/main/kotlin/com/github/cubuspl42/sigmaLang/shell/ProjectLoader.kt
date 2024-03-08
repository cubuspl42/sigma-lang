package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm

class ProjectLoader(
    private val moduleLoader: ModuleLoader,
) {
    fun loadProject(
        mainModulePath: ModulePath,
    ): ProjectTerm {
        val moduleByPath = mutableMapOf<ModulePath, ModuleTerm>()

        fun loadModule(modulePath: ModulePath) {
            if (moduleByPath.containsKey(modulePath)) {
                return
            }

            val moduleTerm = moduleLoader.loadModule(
                modulePath = modulePath,
            )

            moduleByPath[modulePath] = moduleTerm

            moduleTerm.imports.forEach { importTerm ->
                loadModule(importTerm.importedModulePath)
            }
        }

        loadModule(modulePath = mainModulePath)

        return ProjectTerm(
            moduleByPath = moduleByPath,
        )
    }
}
