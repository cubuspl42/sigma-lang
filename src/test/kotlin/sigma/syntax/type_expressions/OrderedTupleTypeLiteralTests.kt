package sigma.syntax.type_expressions

import sigma.StaticTypeScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.TypeReference
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.OrderedTupleTypeLiteral
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpression.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnnamed() {
            val expression = TypeExpression.parse(
                source = "[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteral.Element(
                            name = null,
                            type = TypeReference(
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
            val expression = TypeExpression.parse(
                source = "[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteral.Element(
                            name = null,
                            type = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.Element(
                            name = null,
                            type = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.Element(
                            name = null,
                            type = TypeReference(
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
            val expression = TypeExpression.parse(
                source = "[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeLiteral.Element(
                            name = Symbol.of("a"),
                            type = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.Element(
                            name = null,
                            type = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.Element(
                            name = Symbol.of("c"),
                            type = TypeReference(
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
            val type = TypeExpression.parse(
                source = "[]",
            ).evaluate(
                typeScope = StaticTypeScope.Empty,
            )

            assertEquals(
                expected = OrderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = TypeExpression.parse(
                source = "[a: A, B]",
            ).evaluate(
                typeScope = FixedStaticTypeScope(
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
