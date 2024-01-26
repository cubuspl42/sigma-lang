package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TableValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope

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

    private fun getImportedModuleByName(name: Identifier): Module? =
        importedModulesPaths.firstOrNull { it.name == name.name }?.let { importedModulePath ->
            moduleResolver.resolveModule(modulePath = importedModulePath)
        }

    private val importBlock: StaticBlock = object : StaticBlock() {
        override fun resolveNameLocally(name: Symbol): LeveledResolvedIntroduction? {
            if (name !is Identifier) return null

            return getImportedModuleByName(name = name)?.rootNamespaceBody?.let {
                LeveledResolvedIntroduction(
                    level = StaticScope.Level.Meta,
                    resolvedIntroduction = ResolvedDefinition(body = it),
                )
            }
        }

        override fun getLocalNames(): Set<Symbol> = importedModulesPaths.map {
            Identifier.of(it.name)
        }.toSet()
    }

    private val rootNamespaceBodyLazy = NamespaceDefinitionTerm.analyze(
        context = Expression.BuildContext(
            outerScope = importBlock.chainWith(outerScope),
        ),
        qualifiedPath = modulePath.toQualifiedPath(),
        term = object : NamespaceDefinitionTerm {
            override val name: Identifier = Identifier.of("__root__")

            override val entries: List<NamespaceEntryTerm>
                get() = term.namespaceEntries
        },
    ).namespaceBodyLazy

    val rootNamespaceBody: Expression by rootNamespaceBodyLazy

    val rootNamespace: TableValue = rootNamespaceBody.constClassified!!.value as TableValue

    val innerStaticScope: StaticScope
        get() = rootNamespaceBody.outerScope

    val expressionMap
        get() = rootNamespaceBody.expressionMap

    val errors: Set<SemanticError>
        get() = rootNamespaceBody.errors
}
