package sigma

import sigma.expressions.Expression
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTests {
    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = Symbol("foo"),
                actual = Expression.parse(
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
