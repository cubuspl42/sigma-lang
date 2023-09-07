package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.NamespaceDefinition
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

class Module(
    private val moduleResolver: ModuleResolver,
    private val outerScope: StaticScope,
    private val modulePath: ModulePath,
    private val term: ModuleTerm,
) {
    companion object {
        fun build(
            outerScope: StaticScope?,
            moduleResolver: ModuleResolver,
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
                moduleResolver = moduleResolver,
                outerScope = outerScope,
                modulePath = ModulePath(name = name),
                term = moduleTerm,
            )
        }


        fun build(
            outerScope: StaticScope?,
            moduleResolver: ModuleResolver,
            modulePath: ModulePath,
            term: ModuleTerm,
        ): Module = Module(
            moduleResolver = moduleResolver,
            outerScope = outerScope ?: StaticScope.Empty,
            modulePath = modulePath,
            term = term,
        )
    }

    private val importedModulesPaths = term.imports.map {
        it.modulePath
    }

    private fun getImportedModuleByName(name: Symbol): Module? =
        importedModulesPaths.firstOrNull { it.name == name.name }?.let { importedModulePath ->
            moduleResolver.resolveModule(modulePath = importedModulePath)
        }

    private val importBlock: StaticBlock = object : StaticBlock() {
        override fun resolveNameLocally(name: Symbol): ClassifiedIntroduction? =
            getImportedModuleByName(name = name)?.rootNamespaceDefinition

        override fun getLocalNames(): Set<Symbol> = importedModulesPaths.map {
            Symbol.of(it.name)
        }.toSet()
    }

    val rootNamespaceDefinition = NamespaceDefinition.build(
        outerScope = importBlock.chainWith(outerScope),
        qualifiedPath = modulePath.toQualifiedPath(),
        term = object : NamespaceDefinitionTerm {
            override val name: Symbol = Symbol.of("__root__")

            override val namespaceEntries: List<NamespaceEntryTerm>
                get() = term.namespaceEntries
        },
    )

    val innerStaticScope: StaticScope
        get() = rootNamespaceDefinition.innerStaticScope

    val expressionMap
        get() = rootNamespaceDefinition.expressionMap

    val errors: Set<SemanticError>
        get() = rootNamespaceDefinition.errors
}
