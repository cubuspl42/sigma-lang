package com.github.cubuspl42.sigmaLang.shell

import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.scope.chainWith

data class TransmutationContext(
    val scope: StaticScope,
) {
    fun extendScope(innerScope: StaticScope): TransmutationContext = copy(
        scope = innerScope.chainWith(scope),
    )
}

fun TransmutationContext.withExtendedScope(
    localNames: Set<Identifier>,
    localScopeReference: LocalScope.Reference,
): TransmutationContext = extendScope(
    innerScope = object : StaticScope {
        override fun resolveName(
            referredName: Identifier,
        ): Expression? = if (referredName in localNames) {
            localScopeReference.referDefinitionInitializer(
                name = referredName,
            )
        } else null
    },
)
