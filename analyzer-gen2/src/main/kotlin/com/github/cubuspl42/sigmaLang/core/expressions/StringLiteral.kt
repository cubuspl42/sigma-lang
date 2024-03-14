package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.StringValue
import com.squareup.kotlinpoet.CodeBlock

data class StringLiteral(
    override val value: StringValue,
) : Literal() {
    override fun generateLiteralCode(): CodeBlock = value.generateConstructionCode()
}
