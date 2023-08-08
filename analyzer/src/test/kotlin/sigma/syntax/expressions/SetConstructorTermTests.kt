package sigma.syntax.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.SetValue
import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class SetConstructorTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = SetConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        ReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 1),
                            referee = Symbol.of("foo"),
                        ),
                        ReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 6),
                            referee = Symbol.of("bar"),
                        ),
                        ReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 11),
                            referee = Symbol.of("baz"),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{foo, bar, baz}"),
            )
        }
    }
}
