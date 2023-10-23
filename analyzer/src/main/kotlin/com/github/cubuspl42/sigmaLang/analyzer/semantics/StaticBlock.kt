package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

abstract class StaticBlock : StaticScope {
    class Fixed(
        override val resolvedNameByName: Map<Symbol, ResolvedName>,
    ) : MappingStaticBlock()

    abstract fun resolveNameLocally(name: Symbol): ResolvedName?

    abstract fun getLocalNames(): Set<Symbol>

    final override fun getAllNames(): Set<Symbol> = getLocalNames()

    final override fun resolveName(
        name: Symbol,
    ): ResolvedName? = resolveNameLocally(name = name)

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
