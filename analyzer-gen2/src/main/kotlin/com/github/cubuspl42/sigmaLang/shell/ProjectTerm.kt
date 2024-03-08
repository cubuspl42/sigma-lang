package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.core.ModulePath
import com.github.cubuspl42.sigmaLang.core.ProjectBuilder
import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

class ProjectTerm(
    private val moduleByPath: Map<ModulePath, ModuleTerm>,
) {
    fun build(): ProjectBuilder.Constructor = ProjectBuilder(
        moduleDefinitionBuilders = moduleByPath.entries.mapUniquely { (modulePath, moduleTerm) ->
            ProjectBuilder.ModuleDefinitionBuilder(
                name = modulePath.name, initializer = moduleTerm.transform(),
            )
        },
    ).build()
}
