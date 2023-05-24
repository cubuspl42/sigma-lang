package sigma.syntax.type_expressions

import sigma.TypeReferenceTerm
import sigma.TypeScope
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.TupleType
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteralTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class TupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
                source = "{}",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    orderedEntries = emptyList(),
                    unorderedEntries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleUnorderedEntries() {
            val expression = TypeExpressionTerm.parse(
                source = "{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    orderedEntries = emptyList(),
                    unorderedEntries = listOf(
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("a"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("b"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("c"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 16),
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleOrderedEntry() {
            val expression = TypeExpressionTerm.parse(
                source = "{(a: A)}",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    orderedEntries = listOf(
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("a"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                    ),
                    unorderedEntries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleOrderedEntries() {
            val expression = TypeExpressionTerm.parse(
                source = "{(a: A, b: B, c: C)}",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    orderedEntries = listOf(
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("a"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("b"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        TupleTypeLiteralTerm.Entry(
                            name = Symbol.of("c"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                    unorderedEntries = emptyList(),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val type = TypeExpressionTerm.parse(
                source = "{}",
            ).evaluate(
                typeScope = TypeScope.Empty,
            )

            assertEquals(
                expected = TupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testMultipleUnorderedEntries() {
            val type = TypeExpressionTerm.parse(
                source = "{a: A, b: B, c: C}",
            ).evaluate(
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                        Symbol.of("B") to IntCollectiveType,
                        Symbol.of("C") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = TupleType.unordered(
                    TupleType.UnorderedEntry(
                        name = Symbol.of("a"),
                        type = BoolType,
                    ),
                    TupleType.UnorderedEntry(
                        name = Symbol.of("b"),
                        type = IntCollectiveType,
                    ),
                    TupleType.UnorderedEntry(
                        name = Symbol.of("c"),
                        type = IntCollectiveType,
                    ),
                ),
                actual = type,
            )
        }

        @Test
        fun testMultipleOrderedEntries() {
            val type = TypeExpressionTerm.parse(
                source = "{(a: A, b: B)}",
            ).evaluate(
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                        Symbol.of("B") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = TupleType.ordered(
                    TupleType.OrderedEntry(
                        index = 0,
                        name = Symbol.of("a"),
                        type = BoolType,
                    ),
                    TupleType.OrderedEntry(
                        index = 1,
                        name = Symbol.of("b"),
                        type = IntCollectiveType,
                    ),
                ),
                actual = type,
            )
        }
    }
}
