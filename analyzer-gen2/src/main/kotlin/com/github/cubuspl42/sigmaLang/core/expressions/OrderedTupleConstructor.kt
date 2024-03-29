package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class OrderedTupleConstructor(
    private val elements: List<Lazy<Expression>>,
) : ComplexExpression() {
    companion object {
        fun generateCode(
            elements: List<CodeBlock>,
        ): CodeBlock {
            val passedElementsBuilder = CodeBlock.builder()

            elements.forEach { element ->
                passedElementsBuilder.add(
                    CodeBlock.of(
                        "⇥%L,\n⇤",
                        element,
                    ),
                )
            }

            return CodeBlock.of(
                """
                    %T(
                    ⇥values = listOf(
                    %L),
                    ⇤)
                """.trimIndent(),
                ListValue::class,
                passedElementsBuilder.build(),
            )
        }
    }

    override val subExpressions: Set<Expression>
        get() = elements.map { it.value }.toSet()

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = OrderedTupleConstructor.generateCode(
            elements = elements.map {
                context.getRepresentation(it.value).generateCode()
            },
        )
    }

    override fun bind(context: DynamicContext): Lazy<Value> = lazyOf(
        ListValue(
            values = elements.map {
                it.value.bindStrict(context = context)
            },
        )
    )
}
