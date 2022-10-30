package sigma.expressions

import sigma.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

class IntLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IntLiteral(
                    value = 123,
                ),
                actual = Expression.parse(
                    source = "123",
                ),
            )
        }
    }

    object EvaluationTests {
        @Test
        fun test() {
            assertEquals(
                expected = IntValue(123),
                actual = Expression.parse(source = "123").evaluateAsRoot(),
            )
        }
    }
}
