package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type
import sigma.syntax.TypeAliasDefinitionTerm

class TypeAliasDefinition(
    private val containingModule: Module,
    private val term: TypeAliasDefinitionTerm,
) : StaticDefinition() {
    companion object {
        fun build(
            containingModule: Module,
            term: TypeAliasDefinitionTerm,
        ): TypeAliasDefinition = TypeAliasDefinition(
            containingModule = containingModule,
            term = term,
        )
    }

    override val name: Symbol = term.name

    val aliasedType: Type? by lazy {
        term.definer?.evaluate(
            typeScope = containingModule.innerTypeScope,
        )
    }

    override val errors: Set<SemanticError> = emptySet()
}
