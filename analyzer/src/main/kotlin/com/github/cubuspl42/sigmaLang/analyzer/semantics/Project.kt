package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.getResourceAsText
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ConstantDefinition
import java.lang.IllegalArgumentException

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

    fun toQualifiedPath(): QualifiedPath = QualifiedPath(
        segments = listOf(Symbol.of(name)),
    )
}

class Project(
    private val projectStore: ProjectStore,
    val mainModule: Module,
) {
    companion object {
        fun loadPrelude(): Module {
            val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

            return Module.build(
                outerScope = BuiltinScope,
                moduleResolver = ModuleResolver.Empty,
                source = preludeSource,
                name = "__prelude__",
            )
        }
    }

    class Loader private constructor(
        private val prelude: Module,
    ) {
        companion object {
            fun create(): Loader {
                val prelude = loadPrelude()

                return Loader(
                    prelude = prelude,
                )
            }
        }

        fun load(
            projectStore: ProjectStore,
            mainModuleName: String = "main",
        ): Project {
            val moduleResolver = StoreModuleResolver(
                outerScope = prelude.innerStaticScope,
                store = projectStore,
            )

            val mainModule = moduleResolver.resolveModule(
                modulePath = ModulePath.root(name = mainModuleName),
            ) ?: throw IllegalArgumentException("Could not load main module: $mainModuleName")

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
