package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.Module
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.UnorderedTupleConstructorStub
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazy
import com.squareup.kotlinpoet.CodeBlock

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<Expression>>,
) : ComplexExpression() {
    data class Entry(
        val key: Identifier,
        val value: Lazy<Expression>,
    ) {
        fun asStub() = UnorderedTupleConstructorStub.Entry(
            key = key,
            valueStub = value.value.asStub(),
        )
    }

    companion object {
        val Empty: UnorderedTupleConstructor = UnorderedTupleConstructor(
            valueByKey = emptyMap(),
        )

        fun of(
            vararg entries: Pair<Identifier, Lazy<Expression>>,
        ): UnorderedTupleConstructor = UnorderedTupleConstructor(
            valueByKey = entries.toMap(),
        )

        fun fromEntries(
            entries: Iterable<Entry>,
        ): UnorderedTupleConstructor = UnorderedTupleConstructor(
            valueByKey = entries.associate { it.key to it.value },
        )

        fun generateCode(
            valueByKey: Map<Identifier, CodeBlock>,
        ): CodeBlock {
            val passedEntriesBuilder = CodeBlock.builder()

            valueByKey.forEach { (key, value) ->
                passedEntriesBuilder.add(
                    CodeBlock.of(
                        "⇥%L to %L,\n⇤",
                        key.generateCode(),
                        value,
                    ),
                )
            }

            return CodeBlock.of(
                """
                    %T(
                    ⇥valueByKey = mapOf(
                    %L)
                    ⇤)
                """.trimIndent(),
                UnorderedTuple::class,
                passedEntriesBuilder.build(),
            )
        }
    }

    val values: Collection<Lazy<Expression>>
        get() = valueByKey.values

    override val subExpressions: Set<Expression>
        get() = values.map { it.value }.toSet()

    override fun buildCodegenRepresentation(
        context: Module.CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = UnorderedTupleConstructor.generateCode(
            valueByKey = valueByKey.mapValues { (_, value) ->
                context.getRepresentation(value.value).generateCode().wrapWithLazy()
            },
        )
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        UnorderedTuple(
            valueByKey = valueByKey.mapValues { (_, valueLazy) ->
                lazy {
                    valueLazy.value.bindStrict(scope = scope)
                }
            },
        ),
    )
}
