package sigma

import org.junit.jupiter.api.assertThrows
import sigma.expressions.UnorderedTupleLiteral
import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SourceLocation
import sigma.expressions.UnorderedTupleLiteral.DuplicatedNameError
import sigma.types.BoolType
import sigma.types.IntCollectiveType
import sigma.types.UnorderedTupleType
import sigma.values.FixedStaticValueScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList()
                ),
                actual = Expression.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = UnorderedTupleLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteral.Entry(
                            name = Symbol.of("foo"),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo: baz1}"),
            )
        }

        @Test
        fun testMultipleEntries() {
            assertEquals(
                expected = UnorderedTupleLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteral.Entry(
                            name = Symbol.of("foo"),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleLiteral.Entry(
                            name = Symbol.of("bar"),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo: baz1, bar: baz2}"),
            )
        }
    }

    object TypeCheckingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = emptyMap(),
                ),
                actual = Expression.parse(
                    source = "{}".trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = StaticValueScope.Empty,
                ),
            )
        }

        @Test
        fun testMultipleEntries() {
            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key1") to BoolType,
                        Symbol.of("key2") to IntCollectiveType,
                    ),
                ),
                actual = Expression.parse(
                    source = """
                            {
                                key1: value1,
                                key2: value2,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to IntCollectiveType,
                        ),
                    )
                ),
            )
        }

        @Test
        fun testDuplicatedName() {
            assertThrows<DuplicatedNameError> {
                Expression.parse(
                    source = """
                        {
                            key1: value1,
                            key1: value2,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to BoolType,
                        ),
                    ),
                )
            }
        }
    }
}
