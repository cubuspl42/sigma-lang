package sigma.semantics

import sigma.evaluation.values.Symbol

interface DeclarationScope {

    companion object {
        fun <A> looped(
            build: (DeclarationScope) -> Pair<A, DeclarationScope>,
        ): Pair<A, DeclarationScope> = object : DeclarationScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveDeclaration(
                name: Symbol,
            ): Declaration? = resultScope.resolveDeclaration(name = name)
        }.result
    }

    object Empty : DeclarationScope {
        override fun resolveDeclaration(name: Symbol): Declaration? = null
    }

    class Chained(
        private val outerScope: DeclarationScope,
        private val declarationBlock: DeclarationBlock,
    ) : DeclarationScope {
        override fun resolveDeclaration(name: Symbol): Declaration? =
            declarationBlock.getDeclaration(name = name) ?: outerScope.resolveDeclaration(name = name)
    }

    class Fixed(
        private val declarationByName: Map<Symbol, ValueDeclaration>,
    ) : DeclarationScope {
        companion object {
            fun of(declarations: Set<ValueDeclaration>): Fixed = Fixed(
                declarationByName = declarations.associateBy { it.name },
            )
        }

        override fun resolveDeclaration(name: Symbol): Declaration? = declarationByName[name]
    }

    fun resolveDeclaration(name: Symbol): Declaration?
}
