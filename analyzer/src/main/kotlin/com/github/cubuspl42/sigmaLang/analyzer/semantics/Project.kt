package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
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
    class Loader private constructor(
        private val prelude: Prelude,
    ) {
        companion object {
            fun create(): Loader {
                val prelude = Prelude.load()

                return Loader(
                    prelude = prelude,
                )
            }
        }

        fun load(
            projectStore: ProjectStore,
            mainModuleName: String = "main",
        ): Project {
            val source = projectStore.load(
                modulePath = ModulePath.root(mainModuleName)
            )

            val moduleTerm = ModuleSourceTerm.build(
                ctx = Program.buildParser(
                    sourceName = mainModuleName,
                    source = source,
                ).module(),
            )

            val mainModule = Module.build(
                prelude = prelude,
                term = moduleTerm,
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
