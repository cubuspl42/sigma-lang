package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntType
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
                outerScope = BuiltinScope,
                term = term,
            )

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
                outerScope = StaticScope.Empty,
                term = term,
            )

            assertEquals(
                expected = setOf(
                    IfExpression.InvalidGuardError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
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
                outerScope = StaticScope.Empty,
                term = term,
            )

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    dynamicScope = BuiltinScope,
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
                outerScope = BuiltinScope,
                term = term,
            )

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    dynamicScope = BuiltinScope,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 4L),
                actual = result.value,
            )
        }
    }
}
