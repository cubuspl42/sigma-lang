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
            ): ResolvedName? = resultScope.resolveName(name = name)
        }.result
    }

    object Empty : StaticScope {
        override fun resolveName(name: Symbol): ResolvedName? = null
    }

    class Chained(
        private val outerScope: StaticScope,
        private val staticBlock: StaticBlock,
    ) : StaticScope {
        override fun resolveName(name: Symbol): ResolvedName? =
            staticBlock.resolveNameLocally(name = name) ?: outerScope.resolveName(name = name)
    }

    fun resolveName(name: Symbol): ResolvedName?
}
