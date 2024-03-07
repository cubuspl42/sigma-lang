package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.values.Identifier

class FieldScope(
    private val names: Set<Identifier>,
    private val tupleReference: Reference,
) : StaticScope {
    override fun resolveName(
        referredName: Identifier,
    ): Expression? = if (names.contains(referredName)) {
        tupleReference.readField(
            fieldName = referredName,
        )
    } else {
        null
    }
}
