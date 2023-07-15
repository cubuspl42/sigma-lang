package sigma.semantics

import sigma.evaluation.values.Symbol
import sigma.semantics.types.Type
import sigma.syntax.TypeAliasDefinitionTerm

class TypeAliasDefinition(
    private val containingNamespace: Namespace,
    private val term: TypeAliasDefinitionTerm,
) : StaticDefinition(), TypeDefinition {
    companion object {
        fun build(
            containingNamespace: Namespace,
            term: TypeAliasDefinitionTerm,
        ): TypeAliasDefinition = TypeAliasDefinition(
            containingNamespace = containingNamespace,
            term = term,
        )
    }

    override val name: Symbol = term.name

    val aliasedType: Type by lazy {
        term.definer.evaluateAsType(
            typeScope = containingNamespace.innerTypeScope,
        )
    }

    override val definedType: Type
        get() = aliasedType

    override val errors: Set<SemanticError> = emptySet()
}
