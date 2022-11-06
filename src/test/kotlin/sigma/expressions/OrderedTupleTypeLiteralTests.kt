package sigma.expressions

import org.junit.jupiter.api.Disabled
import sigma.StaticTypeScope
import sigma.TypeExpression
import sigma.TypeReference
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.OrderedTupleType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        @Disabled
        fun testEmpty() {
            val expression = TypeExpression.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        @Disabled
        fun testSingleUnnamed() {
            val expression = TypeExpression.parse(
                source = "[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = null,
                            valueType = TypeReference(
                                referee = Symbol.of("A"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        @Disabled
        fun testAllUnnamed() {
            val expression = TypeExpression.parse(
                source = "[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = null,
                            valueType = TypeReference(
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = null,
                            valueType = TypeReference(
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = null,
                            valueType = TypeReference(
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        @Disabled
        fun testSomeNamed() {
            val expression = TypeExpression.parse(
                source = "[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = Symbol.of("a"),
                            valueType = TypeReference(
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = null,
                            valueType = TypeReference(
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeLiteral.EntryExpression(
                            name = Symbol.of("c"),
                            valueType = TypeReference(
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
        @Disabled
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
        @Disabled
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
                    entries = listOf(
                        OrderedTupleType.Entry(
                            name = Symbol.of("a"),
                            valueType = BoolType,
                        ),
                        OrderedTupleType.Entry(
                            name = null,
                            valueType = IntCollectiveType,
                        ),
                    )
                ),
                actual = type,
            )
        }
    }
}
