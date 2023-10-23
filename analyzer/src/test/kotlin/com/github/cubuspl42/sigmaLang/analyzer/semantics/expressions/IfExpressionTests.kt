package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IfExpressionTests {
    class TypeCheckingTests {
        @Test
        fun testLegalGuard() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %if true (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionSourceTerm

            val call = IfExpression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            assertEquals(
                expected = emptySet(),
                actual = call.directErrors,
            )

            assertIs<IntType>(
                value = call.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testIllegalGuard() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %if 2 (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionSourceTerm

            val call = IfExpression.build(
                context = Expression.BuildContext.Empty,
                term = term,
            ).resolved

            assertEquals(
                expected = setOf(
                    IfExpression.InvalidGuardError(
                        actualType = IntLiteralType(
                            value = IntValue(value = 2L),
                        ),
                    )
                ),
                actual = call.directErrors,
            )

            assertIs<IntType>(
                value = call.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testTrueGuard() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %if true (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionSourceTerm

            val ifExpression = IfExpression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    dynamicScope = DynamicScope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 3L),
                actual = result.value,
            )
        }

        @Test
        fun testFalseGuard() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    %if false (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionSourceTerm

            val ifExpression = IfExpression.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            ).resolved

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    dynamicScope = DynamicScope.Empty,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 4L),
                actual = result.value,
            )
        }
    }
}
