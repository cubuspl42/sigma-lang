package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction

interface StaticScope {
    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveName(
                name: Identifier,
            ): ClassifiedIntroduction? = resultScope.resolveName(name = name)

            override fun getAllNames(): Set<Identifier> = resultScope.getAllNames()
        }.result
    }

    enum class Level {
        Primary, Meta,
    }

    object Empty : StaticScope {
        override fun resolveName(name: Identifier): ClassifiedIntroduction? = null
        override fun getAllNames(): Set<Identifier> = emptySet()
    }

    class Chained(
        private val outerScope: StaticScope,
        private val staticBlock: StaticBlock,
    ) : StaticScope {
        override fun resolveName(name: Identifier): ClassifiedIntroduction? =
            staticBlock.resolveNameLocally(name = name) ?: outerScope.resolveName(name = name)

        override fun getAllNames(): Set<Identifier> = staticBlock.getLocalNames() + outerScope.getAllNames()
    }

    fun resolveName(name: Identifier): ClassifiedIntroduction?

    fun getAllNames(): Set<Identifier>
}
