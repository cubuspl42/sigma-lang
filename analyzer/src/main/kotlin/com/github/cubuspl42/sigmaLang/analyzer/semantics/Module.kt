package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

class Module(
    private val outerScope: StaticScope,
    private val moduleResolver: ModuleResolver,
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
                outerScope = outerScope,
                moduleResolver = moduleResolver,
                term = moduleTerm,
            )
        }


        fun build(
            outerScope: StaticScope?,
            moduleResolver: ModuleResolver,
            term: ModuleTerm,
        ): Module = Module(
            outerScope = outerScope ?: StaticScope.Empty,
            moduleResolver = moduleResolver,
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
        override fun resolveNameLocally(name: Symbol): ClassifiedDeclaration? =
            getImportedModuleByName(name = name)?.rootNamespaceDefinition

        override fun getLocalNames(): Set<Symbol> = importedModulesPaths.map {
            Symbol.of(it.name)
        }.toSet()
    }

    val rootNamespaceDefinition = NamespaceDefinition.build(
        outerScope = importBlock.chainWith(outerScope),
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
