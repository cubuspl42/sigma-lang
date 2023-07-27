package sigma.semantics.expressions

import sigma.evaluation.scope.Scope
import sigma.evaluation.values.IntValue
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.IntLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class IntLiteralTests {
    object EvaluationTests {
        @Test
        fun test() {
            val intLiteral = IntLiteral.build(
                term = ExpressionTerm.parse(source = "123") as IntLiteralTerm,
            )

            assertEquals(
                expected = IntValue(123),
                actual = intLiteral.evaluate(
                    context = EvaluationContext.Initial,
                    scope = Scope.Empty,
                ),
            )
        }
    }
}
