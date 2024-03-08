package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import org.antlr.v4.runtime.Token

data class IdentifierTerm(
    val name: String,
): Wrappable {
    fun transmute(): Identifier = Identifier(
        name = name,
    )

    companion object {
        fun build(
            token: Token,
        ): IdentifierTerm = IdentifierTerm(
            name = token.text,
        )
    }

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("name") to lazyOf(name.wrap()),
        )
    )
}
