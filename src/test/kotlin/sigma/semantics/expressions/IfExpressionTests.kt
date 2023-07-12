package sigma.semantics.expressions

import sigma.semantics.BuiltinTypeScope
import sigma.evaluation.values.IntValue
import sigma.semantics.BuiltinScope
import sigma.semantics.DeclarationScope
import sigma.semantics.types.IntLiteralType
import sigma.semantics.types.IntType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IfExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IfExpressionTests {
    object TypeCheckingTests {
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
                typeScope = BuiltinTypeScope,
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
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
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

    object EvaluationTests {
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
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            assertEquals(
                expected = IntValue(value = 3L),
                actual = ifExpression.evaluate(
                    scope = BuiltinScope,
                ),
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
                typeScope = BuiltinTypeScope,
                declarationScope = BuiltinScope,
                term = term,
            )

            assertEquals(
                expected = IntValue(value = 4L),
                actual = ifExpression.evaluate(
                    scope = BuiltinScope,
                ),
            )
        }
    }
}
