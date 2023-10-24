package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import utils.Matcher

class UnorderedTupleTypeMatcher(
    private val entries: Matcher<Collection<UnorderedTupleType.NamedEntry>>,
) : Matcher<UnorderedTupleType>() {
    class EntryMatcher(
        private val name: Matcher<Symbol>,
        private val type: Matcher<TypeAlike>,
    ) : Matcher<UnorderedTupleType.NamedEntry>() {
        override fun match(actual: UnorderedTupleType.NamedEntry) {
            name.match(actual = actual.name)
            type.match(actual = actual.typeThunk.value!!)
        }
    }

    override fun match(actual: UnorderedTupleType) {
        entries.match(actual = actual.entries)
    }
}
