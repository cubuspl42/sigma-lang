package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.KnotConstructor
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm

class KnotScope(
    private val knotConstructorLazy: Lazy<KnotConstructor>,
) : StaticScope {
    private val knotConstructor by knotConstructorLazy

    override fun resolveName(
        referredName: IdentifierTerm,
    ): StaticScope.ReferenceResolution = knotConstructor.getDefinition(
        identifier = referredName.construct(),
    )?.let {
        StaticScope.DefinitionReference(
            referredKnotLazy = lazyOf(knotConstructor),
        )
    } ?: StaticScope.UnresolvedReference
}
