package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

class UnorderedTupleConstructorTermMatcher(
    val entries: Matcher<List<UnorderedTupleConstructorTerm.Entry>>,
) : Matcher<UnorderedTupleConstructorTerm>() {
    class EntryMatcher(
        val name: Matcher<Identifier>,
        val value: Matcher<ExpressionTerm>,
    ): Matcher<UnorderedTupleConstructorTerm.Entry>() {
        override fun match(actual: UnorderedTupleConstructorTerm.Entry) {
            name.match(actual = actual.name)
            value.match(actual = actual.value)
        }
    }

    override fun match(actual: UnorderedTupleConstructorTerm) {
        entries.match(actual = actual.entries)
    }
}
