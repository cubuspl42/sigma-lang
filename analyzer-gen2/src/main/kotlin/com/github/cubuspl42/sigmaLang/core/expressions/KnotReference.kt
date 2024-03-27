package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class KnotReference(
    private val referredKnotLazy: Lazy<KnotConstructor>,
) : Reference() {

    private val referredKnot: KnotConstructor by referredKnotLazy

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            val referredKnotRepresentation = context.getRepresentation(
                expression = referredKnot,
            ) as KnotConstructor.CodegenRepresentation

            return CodeBlock.of(referredKnotRepresentation.knotName)
        }
    }

    override fun bind(context: DynamicContext): Lazy<Value> {
        val scope = context.scope

        return lazy {
            scope.getValue(referredWrapper = referredKnot)
        }
    }
}
