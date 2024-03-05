package com.github.cubuspl42.sigmaLang.shell.scope

import com.github.cubuspl42.sigmaLang.core.expressions.ArgumentReference
import com.github.cubuspl42.sigmaLang.core.expressions.Reference
import com.github.cubuspl42.sigmaLang.core.values.Identifier

interface StaticScope {
    object Empty : StaticScope {
        override fun resolveName(
            referredName: Identifier,
        ): Reference? = null
    }

    companion object {
        fun argumentScope(
            argumentNames: Set<Identifier>,
            argumentReference: ArgumentReference,
        ): StaticScope = LocalScope(
            names = argumentNames,
            reference = argumentReference,
        )
    }

    fun resolveName(referredName: Identifier): Reference?
}

fun StaticScope.chainWith(
    other: StaticScope,
): StaticScope = object : StaticScope {
    override fun resolveName(referredName: Identifier): Reference? = this@chainWith.resolveName(
        referredName = referredName,
    ) ?: other.resolveName(
        referredName = referredName,
    )
}
