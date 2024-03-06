package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier

class ExpressionScope(
    private val name: Identifier,
    private val boundExpression: Expression,
) : StaticScope {
    override fun resolveName(
        referredName: Identifier,
    ): Expression? = if (referredName == name) {
        boundExpression
    } else {
        null
    }
}
