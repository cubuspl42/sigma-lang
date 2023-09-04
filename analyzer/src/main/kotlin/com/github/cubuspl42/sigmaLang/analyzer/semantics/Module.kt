package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

class Module(
    private val outerScope: StaticScope,
    private val term: ModuleTerm,
) {
    companion object {
        fun build(
            outerScope: StaticScope?,
            term: ModuleTerm,
        ): Module = Module(
            outerScope = outerScope ?: StaticScope.Empty,
            term = term,
        )
    }

    val rootNamespaceDefinition = NamespaceDefinition.build(
        outerScope = outerScope,
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
