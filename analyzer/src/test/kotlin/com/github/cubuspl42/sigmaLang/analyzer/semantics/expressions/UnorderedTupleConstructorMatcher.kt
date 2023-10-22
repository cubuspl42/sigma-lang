package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import utils.Matcher

class UnorderedTupleConstructorMatcher(
    private val entries: Matcher<Set<UnorderedTupleConstructor.Entry>>,
) : Matcher<UnorderedTupleConstructor>() {
    class EntryMatcher(
        private val name: Matcher<Symbol>,
        private val value: Matcher<Expression>,
    ) : Matcher<UnorderedTupleConstructor.Entry>() {
        override fun match(actual: UnorderedTupleConstructor.Entry) {
            name.match(actual = actual.name)
            value.match(actual = actual.value)
        }
    }

    override fun match(actual: UnorderedTupleConstructor) {
        entries.match(actual = actual.entries)
    }
}
