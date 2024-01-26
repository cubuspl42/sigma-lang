package com.github.cubuspl42.sigmaLang.analyzer.syntax.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedIntroduction

// TODO: Move to `syntax`?
interface StaticScope {
    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveNameLeveled(
                name: Symbol,
            ): LeveledResolvedIntroduction? = resultScope.resolveNameLeveled(name = name)

            override fun getAllNames(): Set<Symbol> = resultScope.getAllNames()
        }.result
    }

    enum class Level {
        Primary, Meta,
    }

    object Empty : StaticScope {
        override fun resolveNameLeveled(name: Symbol): LeveledResolvedIntroduction? = null

        override fun getAllNames(): Set<Symbol> = emptySet()
    }

    class Chained(
        private val outerScope: StaticScope,
        private val staticBlock: StaticBlock,
    ) : StaticScope {
        override fun resolveNameLeveled(name: Symbol): LeveledResolvedIntroduction? =
            staticBlock.resolveNameLocally(name = name) ?: outerScope.resolveNameLeveled(name = name)

        override fun getAllNames(): Set<Symbol> = staticBlock.getLocalNames() + outerScope.getAllNames()
    }

    fun resolveNameLeveled(name: Symbol): LeveledResolvedIntroduction?

    fun getAllNames(): Set<Symbol>
}

fun StaticScope.resolveName(
    name: Symbol,
): ResolvedIntroduction? = resolveNameLeveled(
    name = name,
)?.resolvedIntroduction

data class LeveledResolvedIntroduction(
    val level: StaticScope.Level,
    val resolvedIntroduction: ResolvedIntroduction,
) {
    companion object {
        fun primaryIntroduction(
            resolvedIntroduction: ResolvedIntroduction,
        ): LeveledResolvedIntroduction = LeveledResolvedIntroduction(
            level = StaticScope.Level.Primary,
            resolvedIntroduction = resolvedIntroduction,
        )

        fun metaIntroduction(
            resolvedIntroduction: ResolvedIntroduction,
        ): LeveledResolvedIntroduction = LeveledResolvedIntroduction(
            level = StaticScope.Level.Meta,
            resolvedIntroduction = resolvedIntroduction,
        )
    }
}
