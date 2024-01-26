package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope

interface ModuleResolver {
    object Empty : ModuleResolver {
        override fun resolveModule(modulePath: ModulePath): Module? = null
    }

    fun resolveModule(
        modulePath: ModulePath,
    ): Module?
}

class StoreModuleResolver(
    private val store: ProjectStore,
    private val outerScope: StaticScope?,
) : ModuleResolver {
    override fun resolveModule(
        modulePath: ModulePath,
    ): Module? = store.load(
        modulePath = modulePath,
    )?.let { moduleSource ->
        Module.build(
            outerScope = outerScope,
            moduleResolver = this,
            source = moduleSource,
            name = modulePath.name,
        )
    }
}
