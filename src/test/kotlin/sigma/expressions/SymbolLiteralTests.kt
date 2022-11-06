package sigma.expressions

import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = SymbolLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    symbol = Symbol.of("foo"),
                ),
                actual = Expression.parse(
                    source = "`foo`",
                ),
            )
        }
    }
}
