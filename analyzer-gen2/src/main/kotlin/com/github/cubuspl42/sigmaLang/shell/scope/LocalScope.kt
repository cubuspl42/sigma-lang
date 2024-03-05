package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope

class LocalScope(
    private val names: Set<Identifier>,
    private val reference: Reference,
) : StaticScope {
    override fun resolveName(referredName: Identifier): Reference? {
        if (names.contains(referredName)) {
            return reference
        } else {
            return null
        }
    }
}
