package com.github.cubuspl42.sigmaLang.analyzer.syntax.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock

abstract class MappingStaticBlock : StaticBlock() {
    protected abstract val resolvedNameByName: Map<Symbol, LeveledResolvedIntroduction>

    override fun resolveNameLocally(
        name: Symbol,
    ): LeveledResolvedIntroduction? = resolvedNameByName[name]

    override fun getLocalNames(): Set<Symbol> = resolvedNameByName.keys
}
