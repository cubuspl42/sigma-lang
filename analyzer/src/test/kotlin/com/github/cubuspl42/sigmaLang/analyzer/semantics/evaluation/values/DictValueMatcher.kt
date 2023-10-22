package com.github.cubuspl42.sigmaLang.analyzer.semantics.evaluation.values

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.PrimitiveValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import utils.CollectionMatchers
import utils.Matcher

object DictValueMatchers {
    class EntryMatcher(
        private val key: Matcher<PrimitiveValue>,
        private val value: Matcher<Value>,
    ) : Matcher<DictValue.Entry>() {
        constructor(
            index: Long,
            value: Matcher<Value>,
        ) : this(
            key = Matcher.Equals(IntValue(value = index)),
            value = value,
        )

        override fun match(actual: DictValue.Entry) {
            key.match(actual = actual.key)
            value.match(actual = actual.valueThunk.value!!)
        }
    }

    fun eachOnce(
        entries: Set<EntryMatcher>,
    ): Matcher<DictValue> {
        val collectionMatcher = CollectionMatchers.eachOnce(elements = entries)

        return object : Matcher<DictValue>() {
            override fun match(actual: DictValue) {
                collectionMatcher.match(actual.entries)
            }
        }
    }
}
