package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.FieldReadSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FieldReadTests {

    class EvaluationTests {
        @Test
        fun testSimple() {
            val foo = DictValue(
                entries = mapOf(
                    Symbol.of("bar") to IntValue(value = 123L),
                ),
            )

            val fieldRead = FieldRead.build(
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse("foo.bar") as FieldReadSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                fieldRead.bind(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("foo") to foo,
                        )
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 123L),
                actual = result.value,
            )
        }
    }

}
