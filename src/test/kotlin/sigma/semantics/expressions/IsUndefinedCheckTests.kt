package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.DictValue
import sigma.evaluation.values.ValueResult
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IsUndefinedCheckTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IsUndefinedCheckTests {
    object TypeCheckingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
                source = "isUndefined foo",
            ) as IsUndefinedCheckTerm

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

    object EvaluationTests {
        @Test
        fun testNotUndefined() {
            val isUndefinedCheck = IsUndefinedCheck.build(
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse(
                    source = "isUndefined 0",
                ) as IsUndefinedCheckTerm,
            )

            val result = assertIs<ValueResult>(
                isUndefinedCheck.evaluate(
                    context = EvaluationContext.Initial,
                    scope = Scope.Empty,
                ),
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
                term = ExpressionTerm.parse(
                    source = "isUndefined d(0)",
                ) as IsUndefinedCheckTerm,
            )

            val result = assertIs<ValueResult>(
                isUndefinedCheck.evaluate(
                    context = EvaluationContext.Initial,
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("d") to dictValue,
                        ),
                    ),
                ),
            )

            assertEquals(
                expected = BoolValue.True,
                actual = result.value,
            )
        }
    }
}
