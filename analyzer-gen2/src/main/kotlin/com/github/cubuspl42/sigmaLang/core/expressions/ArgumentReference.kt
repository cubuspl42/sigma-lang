package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class ArgumentReference(
    private val referredAbstractionLazy: Lazy<AbstractionConstructor>,
) : Reference() {
    private val referredAbstraction by referredAbstractionLazy

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            val referredAbstractionRepresentation = context.getRepresentation(
                expression = referredAbstraction,
            ) as AbstractionConstructor.CodegenRepresentation

            return CodeBlock.of(referredAbstractionRepresentation.argumentName)
        }
    }

    override fun bind(context: DynamicContext): Lazy<Value> {
        val scope = context.scope

        return lazy {
            scope.getValue(referredWrapper = referredAbstraction)
        }
    }
}
