package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm

class ArgumentScope(
    private val argumentNames: Set<Identifier>,
    private val abstractionConstructorLazy: Lazy<AbstractionConstructor>,
) : StaticScope {

    override fun resolveName(referredName: IdentifierTerm): StaticScope.ReferenceResolution =
        if (argumentNames.contains(referredName.transmute())) {
            StaticScope.ArgumentReference(
                referredAbstractionLazy = abstractionConstructorLazy,
            )
        } else {
            StaticScope.UnresolvedReference
        }
}
