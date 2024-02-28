package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm

interface StaticScope {
    object Empty : StaticScope {
        override fun resolveName(
            referredName: IdentifierTerm,
        ): ReferenceResolution = UnresolvedReference
    }

    companion object {
        fun <A> looped(
            build: (StaticScope) -> Pair<A, StaticScope>,
        ): Pair<A, StaticScope> = object : StaticScope {
            val result = build(this)

            val resultScope = result.second

            override fun resolveName(
                referredName: IdentifierTerm,
            ): ReferenceResolution = resultScope.resolveName(referredName = referredName)
        }.result
    }

    sealed interface ReferenceResolution

    sealed interface ResolvedReference : ReferenceResolution

    data class ArgumentReference(
        val referredAbstractionLazy: Lazy<AbstractionConstructor>,
    ) : ResolvedReference {
        val referredAbstraction by referredAbstractionLazy
    }

    data class DefinitionReference(
        val referredBodyLazy: Lazy<Expression>,
    ) : ResolvedReference {
        val referredBody by referredBodyLazy
    }

    data object UnresolvedReference : ReferenceResolution

    fun resolveName(referredName: IdentifierTerm): ReferenceResolution
}

fun StaticScope.chainWith(
    other: StaticScope,
): StaticScope = object : StaticScope {
    override fun resolveName(referredName: IdentifierTerm): StaticScope.ReferenceResolution =
        when (val resolution = this@chainWith.resolveName(referredName = referredName)) {
            is StaticScope.ResolvedReference -> resolution
            StaticScope.UnresolvedReference -> other.resolveName(referredName = referredName)
        }
}
