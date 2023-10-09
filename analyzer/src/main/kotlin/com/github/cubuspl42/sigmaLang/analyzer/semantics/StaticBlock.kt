package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction

abstract class StaticBlock : StaticScope {
    abstract fun resolveNameLocally(name: Symbol): ClassifiedIntroduction?

    abstract fun getLocalNames(): Set<Symbol>

    final override fun getAllNames(): Set<Symbol> = getLocalNames()

    final override fun resolveName(
        name: Symbol,
    ): ClassifiedIntroduction? = resolveNameLocally(name = name)

    fun chainWith(outerScope: StaticScope): StaticScope = StaticScope.Chained(
        outerScope = outerScope,
        staticBlock = this,
    )
}

fun StaticBlock?.chainWithIfNotNull(
    outerScope: StaticScope,
): StaticScope = when {
    this != null -> {
        StaticScope.Chained(
            outerScope = outerScope,
            staticBlock = this,
        )
    }

    else -> {
        outerScope
    }
}
