package sigma.semantics

import sigma.TypeScope
import sigma.syntax.ModuleTerm

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

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
