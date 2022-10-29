package sigma.expressions

import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolLiteralTests {
    object ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = SymbolLiteral.of("foo"),
                actual = Expression.parse(
                    source = "`foo``",
                ),
            )
        }
    }
}
