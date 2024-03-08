package com.github.cubuspl42.sigmaLang.core.values

import com.github.cubuspl42.sigmaLang.core.expressions.IdentifierLiteral
import com.squareup.kotlinpoet.CodeBlock

data class Identifier(
    val name: String,
) : PrimitiveValue() {
    fun generateCode(): CodeBlock = CodeBlock.of(
        """
            %T(name = %S)
        """.trimIndent(),
        Identifier::class,
        name,
    )

    override fun toLiteral() = IdentifierLiteral(
        value = this,
    )

    companion object {
        fun of(name: String): Identifier = Identifier(name = name)
    }
}
