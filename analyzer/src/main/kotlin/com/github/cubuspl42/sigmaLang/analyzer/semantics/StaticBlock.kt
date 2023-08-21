package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

abstract class StaticBlock : StaticScope {
    abstract fun resolveNameLocally(name: Symbol): ResolvedName?

    final override fun resolveName(
        name: Symbol,
    ): ResolvedName? = resolveNameLocally(name = name)

    fun chainWith(outerScope: StaticScope): StaticScope = StaticScope.Chained(
        outerScope = outerScope,
        staticBlock = this,
    )
}
