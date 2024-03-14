package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.BooleanLiteral
import com.squareup.kotlinpoet.CodeBlock

data class BooleanValue(
    private val value: Boolean,
) : PrimitiveValue() {
    companion object {
        val False = BooleanValue(value = false)
        val True = BooleanValue(value = true)
    }

    fun isTrue(): Boolean = value

    fun generateConstructionCode(): CodeBlock = CodeBlock.of(
        """
            %T(value = $value)
        """.trimIndent(),
        BooleanValue::class,
    )

    override fun toLiteral() = BooleanLiteral(
        value = this,
    )
}
