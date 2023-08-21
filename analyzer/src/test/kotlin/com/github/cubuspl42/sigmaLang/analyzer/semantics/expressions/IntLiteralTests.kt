package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IntLiteralTests {
    class EvaluationTests {
        @Test
        fun test() {
            val intLiteral = IntLiteral.build(
                term = ExpressionSourceTerm.parse(source = "123") as IntLiteralSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                intLiteral.bind(
                    scope = Scope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(123),
                actual = result.value,
            )
        }
    }
}
