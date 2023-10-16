package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.TypeVariableDefinition

abstract class StaticBlock : StaticScope {
    class Fixed(
        private val definitions: Set<Definition>,
    ) : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): Definition? = definitions.find { it.name == name }

        override fun getLocalNames(): Set<Symbol> = definitions.map { it.name }.toSet()
    }

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
