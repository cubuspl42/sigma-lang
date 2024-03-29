package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier

interface StaticScope {
    object Empty : StaticScope {
        override fun resolveName(
            referredName: Identifier,
        ): Expression? = null
    }

    companion object {
        fun fixed(
            expressionByName: Map<Identifier, Expression>,
        ): StaticScope = object : StaticScope {
            override fun resolveName(
                referredName: Identifier,
            ): Expression? = expressionByName[referredName]
        }

        fun argumentScope(
            argumentNames: Set<Identifier>,
            argumentReference: ArgumentReference,
        ): StaticScope = FieldScope(
            names = argumentNames,
            tupleReference = argumentReference,
        )
    }

    fun resolveName(referredName: Identifier): Expression?
}

fun StaticScope.chainWith(
    other: StaticScope,
): StaticScope = object : StaticScope {
    override fun resolveName(referredName: Identifier): Expression? = this@chainWith.resolveName(
        referredName = referredName,
    ) ?: other.resolveName(
        referredName = referredName,
    )
}
