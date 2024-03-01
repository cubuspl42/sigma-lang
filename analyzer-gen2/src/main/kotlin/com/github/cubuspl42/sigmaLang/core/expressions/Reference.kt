package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class Reference(
    private val referredAbstractionLazy: Lazy<AbstractionConstructor>,
) : Expression() {
    private val referredAbstraction by referredAbstractionLazy

    override val subExpressions: Set<Expression> = emptySet()

    override fun buildInnerCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): InnerCodegenRepresentation = object : InnerCodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            val referredAbstractionRepresentation = context.getRepresentation(
                expression = referredAbstraction,
            ).innerRepresentation as AbstractionConstructor.CodegenRepresentation

            return CodeBlock.of(referredAbstractionRepresentation.argumentName)
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazy {
        scope.getArgumentValue(referredAbstraction)
    }
}
