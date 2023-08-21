package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldReadSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = ExpressionSourceTerm.parse("foo.bar"),
            )
        }
    }
}
