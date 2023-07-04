package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTermTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldReadTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = ExpressionTerm.parse("foo.bar"),
            )
        }
    }
}
