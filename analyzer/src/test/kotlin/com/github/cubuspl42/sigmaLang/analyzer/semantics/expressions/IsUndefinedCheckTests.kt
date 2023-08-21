package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IsUndefinedCheckTests {
    class TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "%isUndefined foo",
            ) as IsUndefinedCheckSourceTerm

            val isUndefinedCheck = IsUndefinedCheck.build(
                declarationScope = StaticScope.Empty,
                term = term,
            )

            assertEquals(
                expected = BoolType,
                actual = isUndefinedCheck.inferredType.value,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testNotUndefined() {
            val isUndefinedCheck = IsUndefinedCheck.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "%isUndefined 0",
                ) as IsUndefinedCheckSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                isUndefinedCheck.bind(
                    scope = Scope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = BoolValue.False,
                actual = result.value,
            )
        }

        @Test
        fun testUndefined() {
            val dictValue = DictValue.Empty

            val isUndefinedCheck = IsUndefinedCheck.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "%isUndefined d(0)",
                ) as IsUndefinedCheckSourceTerm,
            )

            val result = assertIs<EvaluationResult<Value>>(
                isUndefinedCheck.bind(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("d") to dictValue,
                        ),
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = BoolValue.True,
                actual = result.value,
            )
        }
    }
}