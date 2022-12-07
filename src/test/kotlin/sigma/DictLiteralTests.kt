package sigma

import org.junit.jupiter.api.assertThrows
import sigma.expressions.DictLiteral
import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SourceLocation
import sigma.types.BoolType
import sigma.types.DictType
import sigma.types.IntCollectiveType
import sigma.values.FixedStaticValueScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object DictLiteralTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        DictLiteral.Association(
                            key = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referee = Symbol.of("foo"),
                            ),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referee = Symbol.of("value1"),
                            ),
                        ),
                        DictLiteral.Association(
                            key = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz"),
                            ),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 23),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("{[foo]: value1, [baz]: value2}"),
            )
        }
    }

    object TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = Expression.parse(
                    source = """
                            {
                                [key1]: value1,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("key1") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                        ),
                    ),
                ),
            )
        }

        @Test
        fun testMultipleEntriesCompatibleEntries() {
            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = Expression.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("key1") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                            Symbol.of("key2") to IntCollectiveType,
                            Symbol.of("value2") to BoolType,
                        ),
                    ),
                ),
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleKeys() {
            assertThrows<DictLiteral.InconsistentKeyTypesError> {
                Expression.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("key1") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                            Symbol.of("key2") to BoolType,
                            Symbol.of("value2") to BoolType,
                        ),
                    ),
                )
            }
        }

        @Test
        fun testMultipleEntriesIncompatibleValues() {
            assertThrows<DictLiteral.InconsistentValueTypesError> {
                Expression.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("key1") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                            Symbol.of("key2") to IntCollectiveType,
                            Symbol.of("value2") to Arbitrary.unorderedTupleType,
                        ),
                    ),
                )
            }
        }

        @Test
        fun testMultipleEntriesNonPrimitiveKey() {
            assertThrows<DictLiteral.NonPrimitiveKeyTypeError> {
                Expression.parse(
                    source = """
                            {
                                [key1]: value1,
                            }
                        """.trimIndent(),
                ).inferType(
                    typeScope = BuiltinTypeScope,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("key1") to Arbitrary.unorderedTupleType,
                            Symbol.of("value1") to BoolType,
                        ),
                    ),
                )
            }
        }
    }
}
