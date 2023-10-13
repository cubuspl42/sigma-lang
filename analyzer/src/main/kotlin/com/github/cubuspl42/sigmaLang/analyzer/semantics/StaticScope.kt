package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction

// TODO: Nuke again?
sealed interface ResolvedName

data class ResolvedDeclaration(
    val declaration: Declaration,
) : ResolvedName

data class ResolvedDefinition(
    val bodyStub: Expression.Stub<Expression>,
) : ResolvedName

interface StaticScope {
    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveName(
                name: Symbol,
            ): Introduction? = resultScope.resolveName(name = name)

            override fun getAllNames(): Set<Symbol> = resultScope.getAllNames()
        }.result
    }

    enum class Level {
        Primary, Meta,
    }

    object Empty : StaticScope {
        override fun resolveName(name: Symbol): Introduction? = null

        override fun getAllNames(): Set<Symbol> = emptySet()
    }

    class Chained(
        private val outerScope: StaticScope,
        private val staticBlock: StaticBlock,
    ) : StaticScope {
        override fun resolveName(name: Symbol): Introduction? =
            staticBlock.resolveNameLocally(name = name) ?: outerScope.resolveName(name = name)

        override fun getAllNames(): Set<Symbol> = staticBlock.getLocalNames() + outerScope.getAllNames()
    }

    fun resolveName(name: Symbol): Introduction?

    fun getAllNames(): Set<Symbol>
}
