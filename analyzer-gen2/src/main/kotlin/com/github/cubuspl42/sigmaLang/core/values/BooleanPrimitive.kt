package com.github.cubuspl42.sigmaLang.core.values

import com.squareup.kotlinpoet.CodeBlock

data class BooleanPrimitive(
    val value: Boolean,
) : Value() {
    companion object {
        val False = BooleanPrimitive(value = false)
        val True = BooleanPrimitive(value = true)
    }

    fun generateConstructionCode(): CodeBlock = CodeBlock.of(
        """
            %T(value = $value)
        """.trimIndent(),
        BooleanPrimitive::class,
    )
}
