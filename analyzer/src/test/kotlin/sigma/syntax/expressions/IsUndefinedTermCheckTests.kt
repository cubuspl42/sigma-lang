package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedTermCheckTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = ExpressionTerm.parse(
                source = "%isUndefined foo",
            )

            assertEquals(
                expected = IsUndefinedCheckTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argument = ReferenceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 13),
                        referee = Symbol.of("foo"),
                    ),
                ),
                actual = term,
            )
        }
    }
}
