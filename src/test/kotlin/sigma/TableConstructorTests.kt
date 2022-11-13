package sigma

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.assertThrows
import sigma.expressions.UnorderedTupleLiteral
import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SourceLocation
import sigma.expressions.UnorderedTupleLiteral.DuplicateKeyError
import sigma.expressions.UnorderedTupleLiteral.InconsistentValuesError
import sigma.expressions.UnorderedTupleLiteral.InconsistentKeysError
import sigma.expressions.UnorderedTupleLiteral.UnsupportedValueError
import sigma.expressions.UnorderedTupleLiteral.UnsupportedKeyError
import sigma.types.BoolType
import sigma.types.AbstractionType
import sigma.types.DictType
import sigma.types.IntCollectiveType
import sigma.types.UnorderedTupleType
import sigma.types.UndefinedType
import sigma.values.FixedStaticValueScope
import sigma.values.IntValue
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO: Rename
object TableConstructorTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = UnorderedTupleLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteral.NamedEntryExpression(
                            name = Symbol.of("foo"),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleLiteral.NamedEntryExpression(
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

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testWithArbitrary() {
            assertEquals(
                expected = UnorderedTupleLiteral(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteral.NamedEntryExpression(
                            name = Symbol.of("foo"),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleLiteral.ArbitraryEntryExpression(
                            key = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 13),
                                referee = Symbol.of("baz"),
                            ),
                            value = Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 19),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo: baz1, [baz]: baz2}"),
            )
        }

        @Test
        fun testMixedSyntax() {
            val exception = assertThrows<IllegalArgumentException> {
                Expression.parse(
                    source = """
                        {
                            key1: value1,
                            [key2]: value2,           
                        }
                    """.trimIndent(),
                )
            }

            println(exception)
        }
    }

    object TypeCheckingTests {
        object SymbolKeysTests {
            @Test
            fun testSymbolKeys1() {
                assertEquals(
                    expected = UnorderedTupleType(
                        valueTypeByKey = mapOf(
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
                        typeScope = GlobalTypeScope, valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to IntCollectiveType,
                            ),
                        )
                    ),
                )
            }

            @Test
            fun testDuplicateSymbolKeys() {
                assertThrows<DuplicateKeyError> {
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

        object IntKeysTests {
            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testConsecutiveIntLiteralKeys() {
                // Array<Bool>, { [0]: Bool, [1]: Bool, [2]: Bool }, Dict<Int, Bool>
                val type = Expression.parse(
                    source = """
                        {
                            [0]: value1,
                            [1]: value2,
                            [2]: value3,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to BoolType,
                            Symbol.of("value3") to BoolType,
                        ),
                    ),
                )

                assertEquals(
                    expected = UnorderedTupleType(
                        valueTypeByKey = mapOf(
                            IntValue(0) to BoolType,
                            IntValue(1) to BoolType,
                            IntValue(2) to BoolType,
                        ),
                    ),
                    actual = type,
                )
            }

            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testNonConsecutiveIntLiteralKeys() {
                // { [0]: Bool, [1]: Bool, [3]: Bool }, Dict<Int, Bool>
                assertEquals(
                    expected = UnorderedTupleType(
                        valueTypeByKey = mapOf(
                            IntValue(0) to BoolType,
                            IntValue(1) to BoolType,
                            IntValue(3) to BoolType,
                        ),
                    ),
                    actual = Expression.parse(
                        source = """
                            {
                                [0]: value1,
                                [1]: value2,
                                [3]: value3,
                            }
                        """.trimIndent(),
                    ).inferType(
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to BoolType,
                            ),
                        ),
                    ),
                )
            }

            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testDuplicateIntLiteralKeys() {
                assertThrows<DuplicateKeyError> {
                    Expression.parse(
                        source = """
                        {
                            [0]: value1,
                            [1]: value2,
                            [1]: value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to BoolType,
                            ),
                        ),
                    )
                }
            }

            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testIntLiteralKeysInconsistentValues() {
                // { [0]: Bool, [1]: Bool, [2]: Int }
                assertEquals(
                    expected = UnorderedTupleType(
                        valueTypeByKey = mapOf(
                            IntValue(0) to BoolType,
                            IntValue(1) to BoolType,
                            IntValue(2) to IntCollectiveType,
                        ),
                    ),
                    actual = Expression.parse(
                        source = """
                        {
                            [0]: value1,
                            [1]: value2,
                            [2]: value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to IntCollectiveType,
                            ),
                        ),
                    ),
                )
            }

            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testIntCollectiveKeys() {
                // Dict<Int, Bool>
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
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("key1") to IntCollectiveType,
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                            ),
                        ),
                    ),
                )
            }

            @Test
            // TODO: Re-support dict literals
            @Disabled
            fun testIntCollectiveKeysInconsistentImages() {
                assertThrows<InconsistentValuesError> {
                    Expression.parse(
                        source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                            [key3]: value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("key1") to IntCollectiveType,
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("key3") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to IntCollectiveType,
                            ),
                        ),
                    )
                }
            }

            @Test
            @Disabled
            fun testIntMixedKeys() {
                // Dict<Int, Bool>
                assertEquals(
                    expected = DictType(
                        keyType = IntCollectiveType,
                        valueType = BoolType,
                    ),
                    actual = Expression.parse(
                        source = """
                        {
                            [0]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                    ).inferType(
                        typeScope = StaticTypeScope.Empty,
                        valueScope = FixedStaticValueScope(
                            entries = mapOf(
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                            ),
                        ),
                    ),
                )
            }
        }
    }

    object UnsupportedTablesTests {
        @Test
        @Disabled
        fun testBoolKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("value1") to IntCollectiveType,
                        ),
                    ),
                )
            }
        }

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testClosureKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to AbstractionType(
                                argumentType = IntCollectiveType,
                                imageType = BoolType,
                            ),
                            Symbol.of("value1") to BoolType,
                        ),
                    ),
                )
            }
        }

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testTableKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to UnorderedTupleType(
                                valueTypeByKey = mapOf(
                                    Symbol.of("foo") to BoolType,
                                ),
                            ),
                            Symbol.of("value1") to BoolType,
                        ),
                    ),
                )
            }
        }

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testUndefinedKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to UndefinedType,
                            Symbol.of("value1") to BoolType,
                        ),
                    ),
                )
            }
        }

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testMixedLiteralKeys() {
            // { [0]: Bool, [`key2`]: Bool }
            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByKey = mapOf(
                        IntValue(0) to BoolType,
                        Symbol.of("key2") to IntCollectiveType,
                    ),
                ), actual = Expression.parse(
                    source = """
                        {
                            [0]: value1,
                            [`key2`]: value2,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to IntCollectiveType,
                        ),
                    ),
                )
            )
        }

        @Test
        // TODO: Re-support dict literals
        @Disabled
        fun testInconsistentCollectiveKeys() {
            assertThrows<InconsistentKeysError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("key2") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to BoolType,
                        ),
                    ),
                )
            }
        }

        @Test
        @Disabled
        fun testUndefinedValues() {
            assertThrows<UnsupportedValueError> {
                Expression.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    typeScope = StaticTypeScope.Empty,
                    valueScope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("value1") to UndefinedType,
                        ),
                    ),
                )
            }
        }
    }
}
