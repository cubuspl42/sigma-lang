package sigma.syntax.type_expressions

import sigma.TypeScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteralBodyTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import sigma.syntax.typeExpressions.TupleTypeLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
                source = "%[]",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    body = OrderedTupleTypeLiteralBodyTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = emptyList(),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnnamed() {
            val expression = TypeExpressionTerm.parse(
                source = "%[A]",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    body = OrderedTupleTypeLiteralBodyTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = listOf(
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = null,
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testAllUnnamed() {
            val expression = TypeExpressionTerm.parse(
                source = "%[A, B, C]",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    body = OrderedTupleTypeLiteralBodyTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = listOf(
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = null,
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = null,
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = null,
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                    referee = Symbol.of("C"),
                                ),
                            ),
                        ),
                    )
                ),
                actual = expression,
            )
        }

        @Test
        fun testSomeNamed() {
            val expression = TypeExpressionTerm.parse(
                source = "%[a: A, B, c: C]",
            )

            assertEquals(
                expected = TupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    body = OrderedTupleTypeLiteralBodyTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 1),
                        elements = listOf(
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = Symbol.of("a"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                    referee = Symbol.of("A"),
                                ),
                            ),
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = null,
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                    referee = Symbol.of("B"),
                                ),
                            ),
                            OrderedTupleTypeLiteralBodyTerm.Element(
                                name = Symbol.of("c"),
                                type = TypeReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 14),
                                    referee = Symbol.of("C"),
                                ),
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
            val type = TypeExpressionTerm.parse(
                source = "%[]",
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
            val type = TypeExpressionTerm.parse(
                source = "%[a: A, B]",
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
