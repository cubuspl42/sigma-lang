package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.squareup.kotlinpoet.CodeBlock

data class StringLiteral(
    override val value: StringPrimitive,
) : Literal() {
    override fun generateLiteralCode(): CodeBlock = value.generateConstructionCode()
}
