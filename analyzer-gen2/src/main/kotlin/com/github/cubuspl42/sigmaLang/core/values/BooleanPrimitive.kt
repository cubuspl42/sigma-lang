package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.BooleanLiteral
import com.squareup.kotlinpoet.CodeBlock

data class BooleanPrimitive(
    private val value: Boolean,
) : PrimitiveValue() {
    companion object {
        val False = BooleanPrimitive(value = false)
        val True = BooleanPrimitive(value = true)
    }

    fun isTrue(): Boolean = value

    fun generateConstructionCode(): CodeBlock = CodeBlock.of(
        """
            %T(value = $value)
        """.trimIndent(),
        BooleanPrimitive::class,
    )

    override fun toLiteral() = BooleanLiteral(
        value = this,
    )
}
