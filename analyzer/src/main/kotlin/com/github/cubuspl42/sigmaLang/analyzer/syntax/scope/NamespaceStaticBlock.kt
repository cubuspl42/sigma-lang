package com.github.cubuspl42.sigmaLang.analyzer.syntax.scope

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticBlock

class NamespaceStaticBlock(
    private val introductionByName: Map<Identifier, LeveledResolvedIntroduction>,
) : StaticBlock() {
    override fun resolveNameLocally(
        name: Symbol,
    ): LeveledResolvedIntroduction? = introductionByName[name]

    override fun getLocalNames(): Set<Symbol> = introductionByName.keys
}
