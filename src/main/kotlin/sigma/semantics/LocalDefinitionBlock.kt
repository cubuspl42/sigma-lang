package sigma.semantics

import sigma.TypeScope
import sigma.syntax.DefinitionTerm
import sigma.evaluation.values.Symbol

abstract class DeclarationBlock : DeclarationScope {
    companion object {
        fun looped(
            build: (DeclarationBlock) -> DeclarationBlock,
        ): DeclarationBlock = object : DeclarationBlock() {
            val resultBlock = build(this)

            override fun getDeclaration(name: Symbol): Declaration? =
                resultBlock.getDeclaration(name = name)
        }.resultBlock
    }

    abstract fun getDeclaration(name: Symbol): Declaration?

    final override fun resolveDeclaration(name: Symbol): Declaration? =
        getDeclaration(name = name)

    fun chainWith(outerScope: DeclarationScope): DeclarationScope = DeclarationScope.Chained(
        outerScope = outerScope,
        declarationBlock = this,
    )
}

abstract class DefinitionBlock : DeclarationBlock() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            definitions: List<DefinitionTerm>,
        ): LocalDefinitionBlock = LocalDefinitionBlock(
            typeScope = typeScope,
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    override fun getDeclaration(name: Symbol): Declaration? = getDefinition(name = name)

    abstract fun getDefinition(name: Symbol): Definition?
}

class LocalDefinitionBlock(
    private val typeScope: TypeScope,
    private val declarationScope: DeclarationScope,
    private val declarations: List<DefinitionTerm>,
) : DefinitionBlock() {
    companion object {
        fun build(
            typeScope: TypeScope,
            outerDeclarationScope: DeclarationScope,
            definitions: List<DefinitionTerm>,
        ): LocalDefinitionBlock = LocalDefinitionBlock(
            typeScope = typeScope,
            declarationScope = outerDeclarationScope,
            declarations = definitions,
        )
    }

    private val definitionByName = declarations.associate {
        it.name to LocalDefinition.build(
            typeScope = typeScope,
            declarationScope = declarationScope,
            term = it,
        )
    }

    override fun getDeclaration(name: Symbol): Declaration? = getDefinition(name = name)

    override fun getDefinition(name: Symbol): LocalDefinition? = definitionByName[name]
}
