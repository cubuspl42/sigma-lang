package com.github.cubuspl42.sigmaLang.shell.terms

import org.antlr.v4.runtime.Token

data class IdentifierTerm(
    val content: String,
) {
    companion object {
        fun build(
            token: Token,
        ): IdentifierTerm = IdentifierTerm(
            content = token.text,
        )
    }
}
