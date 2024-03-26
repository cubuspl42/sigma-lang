package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
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

        fun builder(
            elements: Iterable<ExpressionBuilder<Expression>>,
        ): ExpressionBuilder<OrderedTupleConstructor> = object : ExpressionBuilder<OrderedTupleConstructor>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): OrderedTupleConstructor = OrderedTupleConstructor(
                elements = elements.map {
                    lazyOf(it.build(buildContext = buildContext))
                },
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

    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        ListValue(
            values = elements.map {
                it.value.bindStrict(scope = scope)
            },
        )
    )
}
