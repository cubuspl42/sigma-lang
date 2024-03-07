package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.values.Identifier

class LocalScope(
    private val names: Set<Identifier>,
    private val reference: Reference,
) : StaticScope {
    override fun resolveName(referredName: Identifier): Expression? {
        if (names.contains(referredName)) {
            return reference
        } else {
            return null
        }
    }
}
