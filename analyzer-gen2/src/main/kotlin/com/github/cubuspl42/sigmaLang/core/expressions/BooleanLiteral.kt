package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.CodeBlock

data class BooleanLiteral(
    override val value: BooleanPrimitive,
) : Literal() {
    override fun generateLiteralCode(): CodeBlock = value.generateConstructionCode().wrapWithLazyOf()
}
