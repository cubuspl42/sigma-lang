package sigma.semantics

import sigma.TypeScope
import sigma.syntax.DefinitionTerm
import sigma.values.Symbol

interface DeclarationBlock {
    companion object {
        fun looped(
            build: (DeclarationBlock) -> DeclarationBlock,
        ): DeclarationBlock = object : DeclarationBlock {
            val resultBlock = build(this)

            override fun getDeclaration(name: Symbol): Declaration? =
                resultBlock.getDeclaration(name = name)
        }.resultBlock
    }

    fun getDeclaration(name: Symbol): Declaration?
}

class DefinitionBlock(
    private val typeScope: TypeScope,
    private val declarationScope: DeclarationScope,
    private val declarations: List<DefinitionTerm>,
) : DeclarationBlock {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            definitions: List<DefinitionTerm>,
        ): DefinitionBlock = DefinitionBlock(
            typeScope = typeScope,
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName = declarations.associate {
        it.name to Definition.build(
            typeScope = typeScope,
            declarationScope = declarationScope,
            term = it,
        )
    }

    override fun getDeclaration(name: Symbol): Declaration? = getDefinition(name = name)

    fun getDefinition(name: Symbol): Definition? = definitionByName[name]
}
