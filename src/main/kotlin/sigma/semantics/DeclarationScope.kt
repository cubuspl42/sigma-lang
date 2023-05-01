package sigma.semantics

import sigma.values.Symbol

interface DeclarationScope {
    object Empty : DeclarationScope {
        override fun resolveDeclaration(name: Symbol): Declaration? = null
    }

    class Chained(
        private val outerScope: DeclarationScope,
        private val innerBlock: DefinitionBlock,
    ) : DeclarationScope {
        override fun resolveDeclaration(name: Symbol): Declaration? =
            innerBlock.getDefinition(name = name) ?: outerScope.resolveDeclaration(name = name)
    }

    class Fixed(
        private val declarationByName: Map<Symbol, Declaration>,
    ) : DeclarationScope {
        override fun resolveDeclaration(name: Symbol): Declaration? = declarationByName[name]
    }

    fun resolveDeclaration(name: Symbol): Declaration?
}
