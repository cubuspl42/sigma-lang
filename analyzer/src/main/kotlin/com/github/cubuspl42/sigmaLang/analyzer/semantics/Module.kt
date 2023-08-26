package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm

class Module(
    private val prelude: Prelude,
    private val term: ModuleTerm,
) {
    companion object {
        fun build(
            prelude: Prelude,
            term: ModuleTerm,
        ): Module = Module(
            prelude = prelude,
            term = term,
        )
    }

    val rootNamespace = Namespace.build(
        prelude = prelude,
        term = object : NamespaceDefinitionTerm {
            override val name: Symbol = Symbol.of("__root__")

            override val namespaceEntries: List<NamespaceEntryTerm>
                get() = term.namespaceEntries
        },
    )

    val expressionMap
        get() = rootNamespace.expressionMap

    val errors: Set<SemanticError>
        get() = rootNamespace.errors
}
