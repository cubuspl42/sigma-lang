package sigma.expressions

import org.junit.jupiter.api.Disabled
import sigma.StaticTypeScope
import sigma.TypeExpression
import sigma.TypeReference
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
        @Disabled
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
        @Disabled
        fun testNonEmpty() {
            val expression = TypeExpression.parse(
                source = "{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeLiteral.EntryExpression(
                            name = Symbol.of("a"),
                            valueType = TypeReference(
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeLiteral.EntryExpression(
                            name = Symbol.of("b"),
                            valueType = TypeReference(
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeLiteral.EntryExpression(
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
        @Disabled
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
                    valueTypeByKey = mapOf(
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
