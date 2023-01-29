package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

class IntLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IntLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    IntValue(123),
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
