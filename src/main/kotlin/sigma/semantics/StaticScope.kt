package sigma.semantics

import sigma.evaluation.values.Symbol

interface StaticScope {

    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveName(
                name: Symbol,
            ): Declaration? = resultScope.resolveName(name = name)
        }.result
    }

    object Empty : StaticScope {
        override fun resolveName(name: Symbol): Declaration? = null
    }

    class Chained(
        private val outerScope: StaticScope,
        private val declarationBlock: DeclarationBlock,
    ) : StaticScope {
        override fun resolveName(name: Symbol): Declaration? =
            declarationBlock.getDeclaration(name = name) ?: outerScope.resolveName(name = name)
    }

    class Fixed(
        private val declarationByName: Map<Symbol, ValueDeclaration>,
    ) : StaticScope {
        companion object {
            fun of(declarations: Set<ValueDeclaration>): Fixed = Fixed(
                declarationByName = declarations.associateBy { it.name },
            )
        }

        override fun resolveName(name: Symbol): Declaration? = declarationByName[name]
    }

    fun resolveName(name: Symbol): Declaration?
}
