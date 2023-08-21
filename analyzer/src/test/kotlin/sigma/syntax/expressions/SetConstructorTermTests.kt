package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class SetConstructorTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = SetConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 1),
                            referee = Symbol.of("foo"),
                        ),
                        ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 6),
                            referee = Symbol.of("bar"),
                        ),
                        ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 11),
                            referee = Symbol.of("baz"),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("{foo, bar, baz}"),
            )
        }
    }
}
