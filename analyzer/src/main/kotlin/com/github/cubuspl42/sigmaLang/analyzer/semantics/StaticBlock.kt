package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.ClassifiedIntroduction

abstract class StaticBlock : StaticScope {
    abstract fun resolveNameLocally(name: Identifier): ClassifiedIntroduction?

    abstract fun getLocalNames(): Set<Identifier>

    final override fun getAllNames(): Set<Identifier> = getLocalNames()

    final override fun resolveName(
        name: Identifier,
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
