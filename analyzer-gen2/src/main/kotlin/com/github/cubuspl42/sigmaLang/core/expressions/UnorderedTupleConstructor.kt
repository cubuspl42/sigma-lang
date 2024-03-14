package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.buildRaw
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.UnorderedTupleConstructorStub
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazy
import com.squareup.kotlinpoet.CodeBlock

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<ShadowExpression>>,
) : ComplexExpression() {
    data class Entry(
        val key: Identifier,
        val value: Lazy<ShadowExpression>,
    ) {
        data class Builder(
            val key: Identifier,
            val valueBuilder: ExpressionBuilder<ShadowExpression>,
        ) {
            fun build(buildContext: BuildContext) = Entry(
                key = key,
                value = lazyOf(valueBuilder.buildRaw(buildContext)),
            )
        }

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

        fun fromEntries(
            vararg entries: Entry,
        ): UnorderedTupleConstructor = fromEntries(
            entries = entries.asIterable(),
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
                    %L),
                    ⇤)
                """.trimIndent(),
                UnorderedTupleValue::class,
                passedEntriesBuilder.build(),
            )
        }

        fun builder(
            entries: Iterable<Entry.Builder>,
        ): ExpressionBuilder<UnorderedTupleConstructor> = object : ExpressionBuilder<UnorderedTupleConstructor>() {
            override fun build(
                buildContext: Expression.BuildContext,
            ): UnorderedTupleConstructor = UnorderedTupleConstructor.fromEntries(
                entries = entries.map { it.build(buildContext = buildContext) },
            )
        }
    }

    val values: Collection<Lazy<ShadowExpression>>
        get() = valueByKey.values

    override val subExpressions: Set<Expression>
        get() = values.map { it.value.rawExpression }.toSet()

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = UnorderedTupleConstructor.generateCode(
            valueByKey = valueByKey.mapValues { (_, value) ->
                context.getRepresentation(value.value.rawExpression).generateCode().wrapWithLazy()
            },
        )
    }

    override fun bind(scope: DynamicScope): Lazy<Value> = lazyOf(
        UnorderedTupleValue(
            valueByKey = valueByKey.mapValues { (_, valueLazy) ->
                valueLazy.value.rawExpression.bind(scope = scope)
            },
        ),
    )
}
