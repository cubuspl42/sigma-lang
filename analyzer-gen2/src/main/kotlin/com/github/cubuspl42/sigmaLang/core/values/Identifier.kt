package com.github.cubuspl42.sigmaLang.core.values

import com.squareup.kotlinpoet.CodeBlock

data class Identifier(
    val name: String,
): Value() {
    fun generateCode(): CodeBlock = CodeBlock.of(
        """
            %T(name = %S)
        """.trimIndent(),
        Identifier::class,
        name,
    )
}
