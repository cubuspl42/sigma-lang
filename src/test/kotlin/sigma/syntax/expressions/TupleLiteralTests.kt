package sigma.syntax.expressions

import org.junit.jupiter.api.Disabled
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

class TupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    associations = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleOrderedAssignment() {
            val expression = ExpressionTerm.parse(
                source = "[a]",
            )

            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    associations = listOf(
                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 0,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(1, 1),
                                referee = Symbol.of("a"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleOrderedAssignments() {
            val expression = ExpressionTerm.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    associations = listOf(
                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 0,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(1, 1),
                                referee = Symbol.of("a"),
                            ),
                        ),
                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 1,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(1, 4),
                                referee = Symbol.of("b"),
                            ),
                        ),

                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 2,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(1, 7),
                                referee = Symbol.of("c"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnorderedAssignment() {
            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        TupleLiteralTerm.UnorderedAssociation(
                            targetName = Symbol.of("foo"),
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("[foo: baz1]"),
            )
        }

        @Test
        fun testMultipleUnorderedAssignments() {
            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        TupleLiteralTerm.UnorderedAssociation(
                            targetName = Symbol.of("foo"),
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        TupleLiteralTerm.UnorderedAssociation(
                            targetName = Symbol.of("bar"),
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("[foo: baz1, bar: baz2]"),
            )
        }

        @Test
        fun testMultipleMixedAssignments() {
            assertEquals(
                expected = TupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 0,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
                                referee = Symbol.of("a"),
                            ),
                        ),
                        TupleLiteralTerm.OrderedAssociation(
                            targetIndex = 1,
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("b"),
                            ),
                        ),
                        TupleLiteralTerm.UnorderedAssociation(
                            targetName = Symbol.of("foo"),
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        TupleLiteralTerm.UnorderedAssociation(
                            targetName = Symbol.of("bar"),
                            passedValue = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 23),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("[a, b, foo: baz1, bar: baz2]"),
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

            assertEquals(
                expected = ArrayTable(
                    elements = emptyList(),
                ),
                actual = value,
            )
        }

        @Test
        @Disabled
        fun testMultipleOrderedEntries() {
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

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ),
                actual = value,
            )
        }
    }
}
