package sigma.semantics

import sigma.syntax.ModuleTerm
import sigma.evaluation.scope.Scope
import sigma.evaluation.scope.chainWith
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.semantics.types.Type
import sigma.syntax.NamespaceDefinitionTerm

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
        term = NamespaceDefinitionTerm(
            location = term.location,
            name = Symbol.of("__root__"),
            staticStatements = term.staticStatements,
        ),
    )

    val errors: Set<SemanticError>
        get() = rootNamespace.errors
}
