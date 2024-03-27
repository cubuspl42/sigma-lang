package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicContext
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazy
import com.squareup.kotlinpoet.CodeBlock

class UnorderedTupleConstructor(
    private val valueByKey: Map<Identifier, Lazy<Expression>>,
) : ComplexExpression() {
    data class Entry(
        val key: Identifier,
        val value: Lazy<Expression>,
    ) {
        data class Builder(
            val key: Identifier,
            val valueBuilder: ExpressionBuilder<Expression>,
        ) {
            fun build(buildContext: BuildContext) = Entry(
                key = key,
                value = lazyOf(valueBuilder.build(buildContext)),
            )
        }
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

    val values: Collection<Lazy<Expression>>
        get() = valueByKey.values

    override val subExpressions: Set<Expression>
        get() = values.map { it.value }.toSet()

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = object : CodegenRepresentation() {
        override fun generateCode(): CodeBlock = UnorderedTupleConstructor.generateCode(
            valueByKey = valueByKey.mapValues { (_, value) ->
                context.getRepresentation(value.value).generateCode().wrapWithLazy()
            },
        )
    }

    override fun bind(context: DynamicContext): Lazy<Value> = lazyOf(
        UnorderedTupleValue(
            valueByKey = valueByKey.mapValues { (_, valueLazy) ->
                valueLazy.value.bind(context = context)
            },
        ),
    )
}
