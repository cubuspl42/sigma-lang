package sigma.semantics

import sigma.TypeScope
import sigma.syntax.ModuleTerm
import sigma.evaluation.values.Value
import sigma.evaluation.values.tables.Scope

class Module(
    private val term: ModuleTerm,
    private val declarations: Set<Definition>,
) : Entity() {
    companion object {
        fun build(
            typeScope: TypeScope,
            declarationScope: DeclarationScope,
            term: ModuleTerm,
        ): Module = Module(
            term = term,
            declarations = term.declarations.map {
                Definition.build(
                    typeScope = typeScope,
                    declarationScope = declarationScope,
                    term = it,
                )
            }.toSet(),
        )
    }

    fun evaluateDeclaration(
        name: String,
        scope: Scope,
    ): Value {
        return term.evaluateDeclaration(
            name = name,
            scope = scope,
        )
    }

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
