package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.squareup.kotlinpoet.CodeBlock

data class IdentifierLiteral(
    override val value: Identifier,
) : Literal() {
    override fun generateLiteralCode(): CodeBlock = CodeBlock.of(
        "%L",
        value.generateCode()
    )
}
