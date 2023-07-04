package sigma.syntax.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import sigma.evaluation.values.tables.DictTable
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderedTupleConstructorTermTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleConstructorTerm(
                    location = SourceLocation(1, 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleElement() {
            val expression = ExpressionTerm.parse(
                source = "[a]",
            )

            assertEquals(
                expected = OrderedTupleConstructorTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceTerm(
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
            val expression = ExpressionTerm.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = OrderedTupleConstructorTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceTerm(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),

                        ReferenceTerm(
                            location = SourceLocation(1, 4),
                            referee = Symbol.of("b"),
                        ),

                        ReferenceTerm(
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