package sigma.semantics

import sigma.TypeScope
import sigma.syntax.DefinitionTerm
import sigma.values.Symbol

class DefinitionBlock(
    private val typeScope: TypeScope,
    private val outerDeclarationScope: DeclarationScope,
    private val declarations: List<DefinitionTerm>,
) : DeclarationScope, Entity() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            declarations: List<DefinitionTerm>,
        ): DefinitionBlock = DefinitionBlock(
            typeScope = typeScope,
            outerDeclarationScope = outerDeclarationScope,
            declarations = declarations,
        )
    }

    private val definitionByName = declarations.associate {
        it.name to Definition.build(
            typeScope = typeScope,
            declarationScope = this,
            term = it,
        )
    }

    fun getDefinition(name: Symbol): Definition? = definitionByName[name]

    override fun resolveDeclaration(
        name: Symbol,
    ): Declaration? = getDefinition(
        name = name,
    ) ?: outerDeclarationScope.resolveDeclaration(
        name = name,
    )

    override val errors: Set<SemanticError>
        get() = TODO("Not yet implemented")
}
