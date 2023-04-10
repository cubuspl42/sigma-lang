package sigma

import org.junit.jupiter.api.assertThrows
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.UnorderedTupleLiteralTerm.DuplicatedNameError
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.values.FixedStaticValueScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList()
                ),
                actual = ExpressionTerm.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{foo: baz1}"),
            )
        }

        @Test
        fun testMultipleEntries() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("bar"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{foo: baz1, bar: baz2}"),
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
                actual = ExpressionTerm.parse(
                    source = "{}".trimIndent(),
                ).determineType(
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
                actual = ExpressionTerm.parse(
                    source = """
                            {
                                key1: value1,
                                key2: value2,
                            }
                        """.trimIndent(),
                ).determineType(
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
                ExpressionTerm.parse(
                    source = """
                        {
                            key1: value1,
                            key1: value2,
                        }
                    """.trimIndent(),
                ).determineType(
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
