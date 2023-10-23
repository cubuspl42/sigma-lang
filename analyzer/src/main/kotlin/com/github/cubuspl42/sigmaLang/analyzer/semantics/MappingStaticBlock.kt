package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

abstract class MappingStaticBlock : StaticBlock() {
    protected abstract val resolvedNameByName: Map<Symbol, ResolvedName>

    override fun resolveNameLocally(
        name: Symbol,
    ): ResolvedName? = resolvedNameByName[name]

    override fun getLocalNames(): Set<Symbol> = resolvedNameByName.keys
}
