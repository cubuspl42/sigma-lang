package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.utils.mapUniquely

class UnorderedTupleConstructorStub(
    private val valueStubByKey: Map<Identifier, ExpressionStub<ShadowExpression>>,
) : ExpressionStub<UnorderedTupleConstructor>() {
    data class Entry(
        val key: Identifier,
        val valueStub: ExpressionStub<ShadowExpression>,
    )

    companion object {
        fun fromEntries(
            entries: Iterable<Entry>,
        ): UnorderedTupleConstructorStub = UnorderedTupleConstructorStub(
            valueStubByKey = entries.associate {
                it.key to it.valueStub
            },
        )
    }

    val entries: Set<Entry> by lazy {
        valueStubByKey.entries.mapUniquely { (key, valueStub) ->
            Entry(
                key = key,
                valueStub = valueStub,
            )
        }
    }

    val keys: Set<Identifier> by lazy { valueStubByKey.keys }

    override fun transform(
        context: FormationContext,
    ): ExpressionBuilder<UnorderedTupleConstructor> = UnorderedTupleConstructor.builder(
        entries = entries.map {
            UnorderedTupleConstructor.Entry.Builder(
                key = it.key,
                valueBuilder = it.valueStub.transform(context = context),
            )
        },
    )

    fun withEntry(entry: Entry) = UnorderedTupleConstructorStub(
        valueStubByKey = valueStubByKey + (entry.key to entry.valueStub),
    )
}
