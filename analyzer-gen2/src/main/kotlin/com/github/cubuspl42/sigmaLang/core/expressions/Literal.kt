package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

abstract class Literal : Expression() {
    abstract val value: Value

    final override val subExpressions: Set<Expression> = emptySet()

    final override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = generateLiteralCode()
    }

    final override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(value)

    abstract fun generateLiteralCode(): CodeBlock
}
