package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import utils.Matcher

class UnorderedTupleTypeConstructorTermMatcher(
    private val entries: Matcher<List<UnorderedTupleTypeConstructorTerm.Entry>>, 
) : Matcher<UnorderedTupleTypeConstructorTerm>() {
    class EntryMatcher(
        private val name: Matcher<Identifier>,
        private val type: Matcher<ExpressionTerm>,
    ) : Matcher<UnorderedTupleTypeConstructorTerm.Entry>() {
        override fun match(actual: UnorderedTupleTypeConstructorTerm.Entry) {
            name.match(actual = actual.name)
            type.match(actual = actual.type)
        }
    }

    override fun match(actual: UnorderedTupleTypeConstructorTerm) {
        entries.match(actual = actual.entries)
    }
}
