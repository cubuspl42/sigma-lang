package sigma.syntax.type_expressions

import sigma.TypeScope
import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.metaExpressions.OrderedTupleTypeLiteralTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = MetaExpressionTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnnamed() {
            val expression = MetaExpressionTerm.parse(
                source = "[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteralTerm.Element(
                            name = null,
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
                                referee = Symbol.of("A"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testAllUnnamed() {
            val expression = MetaExpressionTerm.parse(
                source = "[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteralTerm.Element(
                            name = null,
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteralTerm.Element(
                            name = null,
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteralTerm.Element(
                            name = null,
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSomeNamed() {
            val expression = MetaExpressionTerm.parse(
                source = "[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteralTerm.Element(
                            name = Symbol.of("a"),
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteralTerm.Element(
                            name = null,
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteralTerm.Element(
                            name = Symbol.of("c"),
                            type = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                referee = Symbol.of("C"),
                            ),
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
            val type = MetaExpressionTerm.parse(
                source = "[]",
            ).evaluate(
                typeScope = TypeScope.Empty,
            )

            assertEquals(
                expected = OrderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = MetaExpressionTerm.parse(
                source = "[a: A, B]",
            ).evaluate(
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                        Symbol.of("B") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("a"),
                            type = BoolType,
                        ),
                        OrderedTupleType.Element(
                            name = null,
                            type = IntCollectiveType,
                        ),
                    )
                ),
                actual = type,
            )
        }
    }
}
