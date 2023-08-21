package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionSourceTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleElement() {
            val expression = ExpressionSourceTerm.parse(
                source = "[a]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceSourceTerm(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleElements() {
            val expression = ExpressionSourceTerm.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = OrderedTupleConstructorSourceTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceSourceTerm(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),

                        ReferenceSourceTerm(
                            location = SourceLocation(1, 4),
                            referee = Symbol.of("b"),
                        ),

                        ReferenceSourceTerm(
                            location = SourceLocation(1, 7),
                            referee = Symbol.of("c"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }
}
