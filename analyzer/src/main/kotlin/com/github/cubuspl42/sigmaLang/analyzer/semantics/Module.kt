package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.syntax.ModuleSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionSourceTerm

class Module(
    private val prelude: Prelude,
    private val term: ModuleSourceTerm,
) {
    companion object {
        fun build(
            prelude: Prelude,
            term: ModuleSourceTerm,
        ): Module = Module(
            prelude = prelude,
            term = term,
        )
    }

    val rootNamespace = Namespace.build(
        prelude = prelude,
        term = NamespaceDefinitionSourceTerm(
            location = term.location,
            name = Symbol.of("__root__"),
            namespaceEntries = term.namespaceEntries,
        ),
    )

    val errors: Set<SemanticError>
        get() = rootNamespace.errors
}
