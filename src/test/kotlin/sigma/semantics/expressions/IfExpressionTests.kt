package sigma.semantics.expressions

import sigma.evaluation.values.IntValue
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.semantics.BuiltinScope
import sigma.semantics.StaticScope
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.IntType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IfExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IfExpressionTests {
    class TypeCheckingTests {
        @Test
        fun testLegalGuard() {
            val term = ExpressionTerm.parse(
                source = """
                    if true (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionTerm

            val call = IfExpression.build(
                declarationScope = BuiltinScope,
                term = term,
            )

            assertEquals(
                expected = emptySet(),
                actual = call.errors,
            )

            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }

        @Test
        fun testIllegalGuard() {
            val term = ExpressionTerm.parse(
                source = """
                    if 2 (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionTerm

            val call = IfExpression.build(
                declarationScope = StaticScope.Empty,
                term = term,
            )

            assertEquals(
                expected = setOf(
                    IfExpression.InvalidGuardError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        actualType = IntLiteralType(
                            value = IntValue(value = 2L),
                        ),
                    )
                ),
                actual = call.errors,
            )

            assertIs<IntType>(
                value = call.inferredType.value,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testTrueGuard() {
            val term = ExpressionTerm.parse(
                source = """
                    if true (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionTerm

            val ifExpression = IfExpression.build(
                declarationScope = StaticScope.Empty,
                term = term,
            )

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    scope = BuiltinScope,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 3L),
                actual = result.value,
            )
        }

        @Test
        fun testFalseGuard() {
            val term = ExpressionTerm.parse(
                source = """
                    if false (
                        %then 3,
                        %else 4,
                    )
                """.trimIndent()
            ) as IfExpressionTerm

            val ifExpression = IfExpression.build(
                declarationScope = BuiltinScope,
                term = term,
            )

            val result = assertIs<EvaluationResult<Value>>(
                ifExpression.bind(
                    scope = BuiltinScope,
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(value = 4L),
                actual = result.value,
            )
        }
    }
}
