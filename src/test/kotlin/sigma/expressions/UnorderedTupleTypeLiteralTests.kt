package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.syntax.typeExpressions.TypeExpression
import sigma.TypeReference
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.UnorderedTupleTypeLiteral
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.UnorderedTupleType
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpression.parse(
                source = "{}",
            )

            assertEquals(
                expected = UnorderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testNonEmpty() {
            val expression = TypeExpression.parse(
                source = "{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeLiteral.Entry(
                            name = Symbol.of("a"),
                            valueType = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeLiteral.Entry(
                            name = Symbol.of("b"),
                            valueType = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeLiteral.Entry(
                            name = Symbol.of("c"),
                            valueType = TypeReference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 16),
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
                source = "{}",
            ).evaluate(
                typeScope = StaticTypeScope.Empty,
            )

            assertEquals(
                expected = UnorderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = TypeExpression.parse(
                source = "{a: A, b: B, c: C}",
            ).evaluate(
                typeScope = FixedStaticTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                        Symbol.of("B") to IntCollectiveType,
                        Symbol.of("C") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("a") to BoolType,
                        Symbol.of("b") to IntCollectiveType,
                        Symbol.of("c") to IntCollectiveType,
                    ),
                ),
                actual = type,
            )
        }
    }
}
