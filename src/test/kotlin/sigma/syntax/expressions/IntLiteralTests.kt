package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

class IntLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IntLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    IntValue(123),
                ),
                actual = ExpressionTerm.parse(
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
                actual = ExpressionTerm.parse(source = "123").evaluateAsRoot(),
            )
        }
    }
}
