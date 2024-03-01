package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.CodeBlock

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<Expression>>,
): ComplexExpression() {
    val values: Collection<Lazy<Expression>>
        get() = valueByKey.values

    override val subExpressions: Set<Expression>
        get() = values.map { it.value }.toSet()

    override fun buildInnerCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): InnerCodegenRepresentation = object : InnerCodegenRepresentation() {
        override fun generateCode(): CodeBlock {
            val passedEntriesBuilder = CodeBlock.builder()

            valueByKey.forEach { (key, value) ->
                passedEntriesBuilder.add(
                    CodeBlock.of(
                        "⇥%L to %L,\n⇤",
                        key.generateCode(),
                        context.getRepresentation(value.value).generateUsage(),
                    )
                )
            }

            return CodeBlock.of(
                """
                    lazyOf(
                    ⇥%T(
                    ⇥valueByKey = mapOf(
                    %L)
                    ⇤)
                    ⇤)
                    """.trimIndent(),
                UnorderedTuple::class,
                passedEntriesBuilder.build(),
            )
        }
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        UnorderedTuple(
            valueByKey = valueByKey.mapValues { (_, valueLazy) ->
                valueLazy.value.bind(scope = scope)
            }
        )
    )
}
