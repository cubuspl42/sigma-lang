package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.BooleanValue
import com.squareup.kotlinpoet.CodeBlock

data class BooleanLiteral(
    override val value: BooleanValue,
) : Literal() {
    override fun generateLiteralCode(): CodeBlock = value.generateConstructionCode()
}
