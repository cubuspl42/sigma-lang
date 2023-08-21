package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolLiteralTermTests {
    class ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = SymbolLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    symbol = Symbol.of("foo"),
                ),
                actual = ExpressionTerm.parse(
                    source = "`foo`",
                ),
            )
        }
    }
}