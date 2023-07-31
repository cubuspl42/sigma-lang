package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.EvaluationResult
import sigma.evaluation.values.Value
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IntLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IntLiteralTests {
    class EvaluationTests {
        @Test
        fun test() {
            val intLiteral = IntLiteral.build(
                term = ExpressionTerm.parse(source = "123") as IntLiteralTerm,
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
