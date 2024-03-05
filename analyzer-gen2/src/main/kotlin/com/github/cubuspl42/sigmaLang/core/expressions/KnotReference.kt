package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class KnotReference(
    private val referredKnotLazy: Lazy<KnotConstructor>,
) : Reference() {

    private val referredKnot: KnotConstructor by referredKnotLazy

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            val referredKnotRepresentation = context.getRepresentation(
                expression = referredKnot,
            ) as KnotConstructor.CodegenRepresentation

            return CodeBlock.of(referredKnotRepresentation.knotName)
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazy {
        scope.getValue(referredWrapper = referredKnot)
    }
}
