package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.getResourceAsText
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm

data class ModulePath(
    /**
     * The name of the module
     */
    val name: String,
) {
    companion object {
        fun root(name: String): ModulePath = ModulePath(
            name = name,
        )
    }
}

class Project(
    private val projectStore: ProjectStore,
    val mainModule: Module,
) {
    companion object {
        private fun loadModule(
            outerScope: StaticScope?,
            source: String,
            name: String,
        ): Module {
            val moduleTerm = ModuleSourceTerm.build(
                ctx = Program.buildParser(
                    sourceName = name,
                    source = source,
                ).module(),
            )

            return Module.build(
                outerScope = outerScope,
                term = moduleTerm,
            )
        }

        fun loadPrelude(): Module {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            return loadModule(
                outerScope = BuiltinScope,
                source = preludeSource,
                name = "__prelude__",
            )
        }
    }

    class Loader private constructor(
        private val module: Module,
    ) {
        companion object {
            fun create(): Loader {
                val prelude = loadPrelude()

                return Loader(
                    module = prelude,
                )
            }
        }

        fun load(
            projectStore: ProjectStore,
            mainModuleName: String = "main",
        ): Project {
            val moduleSource = projectStore.load(
                modulePath = ModulePath.root(mainModuleName)
            )

            val mainModule = loadModule(
                outerScope = module.innerStaticScope,
                source = moduleSource,
                name = mainModuleName,
            )

            return Project(
                projectStore = projectStore,
                mainModule = mainModule,
            )
        }
    }

    val errors: Set<SemanticError>
        get() = mainModule.errors

    val entryPoint: ConstantDefinition
        get() = mainModule.rootNamespaceDefinition.getDefinition(
            name = Symbol.of("main"),
        )!!
}
