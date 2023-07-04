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

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val value = ExpressionTerm.parse(
                source = "[]",
            ).evaluate(
                scope = Scope.Empty,
            ).toEvaluatedValue

            assertIs<DictTable>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = emptyList(),
                ).evaluatedEntries,
                actual = value.evaluatedEntries,
            )
        }

        @Test
        fun testNonEmpty() {
            val value = ExpressionTerm.parse(
                source = "[a, b]",
            ).evaluate(
                scope = FixedScope(
                    entries = mapOf(
                        Symbol.of("a") to BoolValue(false),
                        Symbol.of("b") to IntValue(1),
                    ),
                ),
            ).toEvaluatedValue

            assertIs<DictTable>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ).evaluatedEntries,
                actual = value.evaluatedEntries,
            )
        }
    }
}
