package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.OrderedTupleType
import sigma.values.BoolValue
import sigma.values.FixedStaticValueScope
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.ArrayTable
import sigma.values.tables.FixedScope
import sigma.values.tables.Scope
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = Expression.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleLiteral(
                    location = SourceLocation(1, 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleElement() {
            val expression = Expression.parse(
                source = "[a]",
            )

            assertEquals(
                expected = OrderedTupleLiteral(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        Reference(
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
            val expression = Expression.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = OrderedTupleLiteral(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        Reference(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),

                        Reference(
                            location = SourceLocation(1, 4),
                            referee = Symbol.of("b"),
                        ),

                        Reference(
                            location = SourceLocation(1, 7),
                            referee = Symbol.of("c"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object TypeInferenceTests {
        @Test
        fun testEmpty() {
            val type = Expression.parse(
                source = "[]",
            ).inferType(
                typeScope = StaticTypeScope.Empty,
                valueScope = StaticValueScope.Empty,
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = emptyList(),
                ),
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = Expression.parse(
                source = "[a, b]",
            ).inferType(
                typeScope = StaticTypeScope.Empty,
                valueScope = FixedStaticValueScope(
                    entries = mapOf(
                        Symbol.of("a") to BoolType,
                        Symbol.of("b") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(name = null, type = BoolType),
                        OrderedTupleType.Element(name = null, type = IntCollectiveType),
                    ),
                ),
                actual = type,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val value = Expression.parse(
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
        fun testNonEmpty() {
            val value = Expression.parse(
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
