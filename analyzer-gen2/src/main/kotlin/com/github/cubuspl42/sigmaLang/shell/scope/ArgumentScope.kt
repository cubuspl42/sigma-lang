package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.shell.terms.IdentifierTerm
import com.github.cubuspl42.sigmaLang.shell.terms.UnorderedTupleTypeConstructorTerm

class ArgumentScope(
    private val argumentNames: Set<IdentifierTerm>,
    private val abstractionConstructorLazy: Lazy<AbstractionConstructor>,
) : StaticScope {
    companion object {
        fun construct(
            abstractionConstructorLazy: Lazy<AbstractionConstructor>,
            argumentType: UnorderedTupleTypeConstructorTerm,
        ): ArgumentScope {
            val argumentNames = argumentType.keys

            return ArgumentScope(
                argumentNames = argumentNames,
                abstractionConstructorLazy = abstractionConstructorLazy,
            )
        }
    }

    override fun resolveName(referredName: IdentifierTerm): StaticScope.ReferenceResolution =
        if (argumentNames.contains(referredName)) {
            StaticScope.ArgumentReference(
                referredAbstractionLazy = abstractionConstructorLazy,
            )
        } else {
            StaticScope.UnresolvedReference
        }
}
