package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext

class UnorderedTupleConstructorStub(
    private val valueStubByKey: Map<Identifier, ExpressionStub<*>>,
) : ExpressionStub<UnorderedTupleConstructor>() {
    data class Entry(
        val key: Identifier,
        val valueStub: ExpressionStub<*>,
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

    val keys: Set<Identifier> by lazy { valueStubByKey.keys }

    override fun form(context: FormationContext) = lazyOf(
        UnorderedTupleConstructor(
            valueByKey = valueStubByKey.mapValues { (_, valueStub) ->
                valueStub.form(context = context)
            },
        ),
    )

    fun withEntry(entry: Entry) = UnorderedTupleConstructorStub(
        valueStubByKey = valueStubByKey + (entry.key to entry.valueStub),
    )
}
