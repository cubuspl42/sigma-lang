package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedTermCheckTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IsUndefinedCheckTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argument = ReferenceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 12),
                        referee = Symbol.of("foo"),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "isUndefined foo",
                ),
            )
        }
    }
}
