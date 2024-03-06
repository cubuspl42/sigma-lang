package com.github.cubuspl42.sigmaLang.shell.stubs

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.terms.UnorderedTupleConstructorTerm

class UnorderedTupleConstructorStub(
    private val valueStubByKey: Map<Identifier, ExpressionStub<*>>,
) : ExpressionStub<UnorderedTupleConstructor>() {
    data class Entry(
        val key: Identifier,
        val value: ExpressionStub<*>,
    )

    companion object {
        fun fromEntries(
            entries: List<Entry>,
        ): UnorderedTupleConstructorStub = UnorderedTupleConstructorStub(
            valueStubByKey = entries.associate {
                it.key to it.value
            },
        )
    }

    override fun form(context: FormationContext) = lazyOf(
        UnorderedTupleConstructor(
            valueByKey = valueStubByKey.mapValues { (_, valueStub) ->
                valueStub.form(context = context)
            },
        ),
    )

    fun withEntry(entry: Entry) = UnorderedTupleConstructorStub(
        valueStubByKey = valueStubByKey + (entry.key to entry.value),
    )
}
