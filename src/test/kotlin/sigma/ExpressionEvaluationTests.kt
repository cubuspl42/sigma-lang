package sigma

import sigma.syntax.expressions.ExpressionTerm
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTests {
    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = Symbol("foo"),
                actual = ExpressionTerm.parse(
                    source = """
                        let {
                            n = `foo`,
                            m = n,
                        } in m
                    """.trimIndent()
                ).evaluateAsRoot(),
            )
        }
    }
}
