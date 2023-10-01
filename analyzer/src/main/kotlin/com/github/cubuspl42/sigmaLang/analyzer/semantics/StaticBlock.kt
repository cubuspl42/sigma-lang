package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction

abstract class StaticBlock : StaticScope {
    abstract fun resolveNameLocally(name: Symbol): Introduction?

    abstract fun getLocalNames(): Set<Symbol>

    final override fun getAllNames(): Set<Symbol> = getLocalNames()

    final override fun resolveName(
        name: Symbol,
    ): Introduction? = resolveNameLocally(name = name)

    fun chainWith(outerScope: StaticScope): StaticScope = StaticScope.Chained(
        outerScope = outerScope,
        staticBlock = this,
    )
}
