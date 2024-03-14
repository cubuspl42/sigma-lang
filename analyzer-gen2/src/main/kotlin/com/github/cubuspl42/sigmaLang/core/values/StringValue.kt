package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.StringLiteral
import com.squareup.kotlinpoet.CodeBlock

data class StringValue(
    val value: String,
) : PrimitiveValue() {
    fun generateConstructionCode(): CodeBlock = CodeBlock.of(
        """
            %T(value = %S)
        """.trimIndent(),
        StringValue::class,
        value,
    )

    override fun toLiteral() = StringLiteral(
        value = this,
    )
}
