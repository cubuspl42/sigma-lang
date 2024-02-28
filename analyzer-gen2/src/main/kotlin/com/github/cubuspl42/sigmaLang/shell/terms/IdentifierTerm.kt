package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.Identifier
import org.antlr.v4.runtime.Token

data class IdentifierTerm(
    val name: String,
) {
    fun construct(): Identifier = Identifier(
        name = name,
    )

    companion object {
        fun build(
            token: Token,
        ): IdentifierTerm = IdentifierTerm(
            name = token.text,
        )
    }
}
