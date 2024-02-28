package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm

interface Scope {
    object Empty : Scope {
        override fun resolveName(
            referredName: IdentifierTerm,
        ): ReferenceResolution = UnresolvedReference
    }

    companion object {
        fun <A> looped(
            build: (Scope) -> Pair<A, Scope>,
        ): Pair<A, Scope> = object : Scope {
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

fun Scope.chainWith(
    other: Scope,
): Scope = object : Scope {
    override fun resolveName(referredName: IdentifierTerm): Scope.ReferenceResolution =
        when (val resolution = this@chainWith.resolveName(referredName = referredName)) {
            is Scope.ResolvedReference -> resolution
            Scope.UnresolvedReference -> other.resolveName(referredName = referredName)
        }
}
