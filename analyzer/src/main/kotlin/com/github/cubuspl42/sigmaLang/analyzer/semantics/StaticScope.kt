package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

interface StaticScope {
    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveName(
                name: Symbol,
            ): ClassifiedDeclaration? = resultScope.resolveName(name = name)

            override fun getAllNames(): Set<Symbol> = resultScope.getAllNames()
        }.result
    }

    object Empty : StaticScope {
        override fun resolveName(name: Symbol): ClassifiedDeclaration? = null
        override fun getAllNames(): Set<Symbol> = emptySet()
    }

    class Chained(
        private val outerScope: StaticScope,
        private val staticBlock: StaticBlock,
    ) : StaticScope {
        override fun resolveName(name: Symbol): ClassifiedDeclaration? =
            staticBlock.resolveNameLocally(name = name) ?: outerScope.resolveName(name = name)

        override fun getAllNames(): Set<Symbol> = staticBlock.getLocalNames() + outerScope.getAllNames()
    }

    fun resolveName(name: Symbol): ClassifiedDeclaration?

    fun getAllNames(): Set<Symbol>
}
