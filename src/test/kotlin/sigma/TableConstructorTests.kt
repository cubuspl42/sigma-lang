package sigma

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.assertThrows
import sigma.expressions.TableConstructor
import sigma.expressions.Expression
import sigma.expressions.IntLiteral
import sigma.expressions.Reference
import sigma.expressions.TableConstructor.DuplicateKeyError
import sigma.expressions.TableConstructor.InconsistentValuesError
import sigma.expressions.TableConstructor.InconsistentKeysError
import sigma.expressions.TableConstructor.UnsupportedValueError
import sigma.expressions.TableConstructor.UnsupportedKeyError
import sigma.types.BoolType
import sigma.types.AbstractionType
import sigma.types.DictType
import sigma.types.IntCollectiveType
import sigma.types.IntLiteralType
import sigma.types.StructType
import sigma.types.SymbolType
import sigma.types.UndefinedType
import sigma.values.FixedStaticValueScope
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object TableConstructorTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = TableConstructor(
                    entries = listOf(
                        TableConstructor.SymbolEntryExpression(
                            name = Symbol.of("foo"),
                            value = Reference(Symbol.of("baz1")),
                        ),
                        TableConstructor.SymbolEntryExpression(
                            name = Symbol.of("bar"),
                            value = Reference(Symbol.of("baz2")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, bar = baz2}"),
            )
        }

        @Test
        fun testWithArbitrary() {
            assertEquals(
                expected = TableConstructor(
                    entries = listOf(
                        TableConstructor.SymbolEntryExpression(
                            name = Symbol.of("foo"),
                            value = Reference(Symbol.of("baz1")),
                        ),
                        TableConstructor.ArbitraryEntryExpression(
                            key = Reference(Symbol.of("baz")),
                            value = Reference(Symbol.of("baz2")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, [baz] = baz2}"),
            )
        }

        @Test
        fun testArray() {
            assertEquals(
                expected = TableConstructor(
                    entries = listOf(
                        TableConstructor.ArbitraryEntryExpression(
                            key = IntLiteral.of(0),
                            value = Reference(Symbol.of("foo")),
                        ),
                        TableConstructor.ArbitraryEntryExpression(
                            key = IntLiteral.of(1),
                            value = Reference(Symbol.of("bar")),
                        ),
                        TableConstructor.ArbitraryEntryExpression(
                            key = IntLiteral.of(2),
                            value = Reference(Symbol.of("baz")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo, bar, baz}"),
            )
        }
    }

    object TypeCheckingTests {
        object SymbolKeysTests {
            @Test
            fun testSymbolKeys1() {
                assertEquals(
                    expected = StructType(
                        entries = mapOf(
                            SymbolType.of("key1") to BoolType,
                            SymbolType.of("key2") to IntCollectiveType,
                        )
                    ),
                    actual = Expression.parse(
                        source = """
                        {
                            key1 = value1,
                            key2 = value2,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to IntCollectiveType,
                            ),
                        ).asStaticScope(),
                    ),
                )
            }

            @Test
            fun testSymbolKeys2() {
                assertEquals(
                    expected = StructType(
                        entries = mapOf(
                            SymbolType.of("key1") to BoolType,
                            SymbolType.of("key2") to IntCollectiveType,
                        )
                    ),
                    actual = Expression.parse(
                        source = """
                            {
                                key1 = value1,
                                [`key2`] = value2,
                            }
                        """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to IntCollectiveType,
                            ),
                        ).asStaticScope(),
                    ),
                )
            }

            @Test
            fun testDuplicateSymbolKeys() {
                assertThrows<DuplicateKeyError> {
                    Expression.parse(
                        source = """
                        {
                            key1 = value1,
                            key1 = value2,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                            ),
                        ).asStaticScope(),
                    )
                }
            }
        }

        object IntKeysTests {
            @Test
            fun testConsecutiveIntLiteralKeys() {
                // Array<Bool>, { [0]: Bool, [1]: Bool, [2]: Bool }, Dict<Int, Bool>
                val type = Expression.parse(
                    source = """
                        {
                            [0] = value1,
                            [1] = value2,
                            [2] = value3,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to BoolType,
                            Symbol.of("value3") to BoolType,
                        ),
                    ).asStaticScope(),
                )

                assertEquals(
                    expected = StructType(
                        entries = mapOf(
                            IntLiteralType.of(0) to BoolType,
                            IntLiteralType.of(1) to BoolType,
                            IntLiteralType.of(2) to BoolType,
                        ),
                    ),
                    actual = type,
                )
            }

            @Test
            fun testNonConsecutiveIntLiteralKeys() {
                // { [0]: Bool, [1]: Bool, [3]: Bool }, Dict<Int, Bool>
                assertEquals(
                    expected = StructType(
                        entries = mapOf(
                            IntLiteralType.of(0) to BoolType,
                            IntLiteralType.of(1) to BoolType,
                            IntLiteralType.of(3) to BoolType,
                        ),
                    ),
                    actual = Expression.parse(
                        source = """
                            {
                                [0] = value1,
                                [1] = value2,
                                [3] = value3,
                            }
                        """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to BoolType,
                            ),
                        ).asStaticScope(),
                    ),
                )
            }

            @Test
            fun testDuplicateIntLiteralKeys() {
                assertThrows<DuplicateKeyError> {
                    Expression.parse(
                        source = """
                        {
                            [0] = value1,
                            [1] = value2,
                            [1] = value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to BoolType,
                            ),
                        ).asStaticScope(),
                    )
                }
            }

            @Test
            fun testIntLiteralKeysInconsistentValues() {
                // { [0]: Bool, [1]: Bool, [2]: Int }
                assertEquals(
                    expected = StructType(
                        entries = mapOf(
                            IntLiteralType.of(0) to BoolType,
                            IntLiteralType.of(1) to BoolType,
                            IntLiteralType.of(2) to IntCollectiveType,
                        ),
                    ),
                    actual = Expression.parse(
                        source = """
                        {
                            [0] = value1,
                            [1] = value2,
                            [2] = value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to IntCollectiveType,
                            ),
                        ).asStaticScope(),
                    ),
                )
            }

            @Test
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
                                [key1] = value1,
                                [key2] = value2,
                            }
                        """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("key1") to IntCollectiveType,
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                            ),
                        ).asStaticScope(),
                    ),
                )
            }

            @Test
            fun testIntCollectiveKeysInconsistentImages() {
                assertThrows<InconsistentValuesError> {
                    Expression.parse(
                        source = """
                        {
                            [key1] = value1,
                            [key2] = value2,
                            [key3] = value3,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            mapOf(
                                Symbol.of("key1") to IntCollectiveType,
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("key3") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                                Symbol.of("value3") to IntCollectiveType,
                            ),
                        ).asStaticScope(),
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
                            [0] = value1,
                            [key2] = value2,
                        }
                    """.trimIndent(),
                    ).inferType(
                        scope = FixedStaticValueScope(
                            entries = mapOf(
                                Symbol.of("key2") to IntCollectiveType,
                                Symbol.of("value1") to BoolType,
                                Symbol.of("value2") to BoolType,
                            ),
                        ).asStaticScope(),
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
                            [key1] = value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("value1") to IntCollectiveType,
                        ),
                    ).asStaticScope(),
                )
            }
        }

        @Test
        fun testClosureKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1] = value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to AbstractionType(
                                imageType = BoolType,
                            ),
                            Symbol.of("value1") to BoolType,
                        ),
                    ).asStaticScope(),
                )
            }
        }

        @Test
        fun testTableKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1] = value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to StructType(
                                entries = mapOf(
                                    SymbolType.of("foo") to BoolType,
                                ),
                            ),
                            Symbol.of("value1") to BoolType,
                        ),
                    ).asStaticScope(),
                )
            }
        }

        @Test
        fun testUndefinedKeys() {
            assertThrows<UnsupportedKeyError> {
                Expression.parse(
                    source = """
                        {
                            [key1] = value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to UndefinedType,
                            Symbol.of("value1") to BoolType,
                        ),
                    ).asStaticScope(),
                )
            }
        }

        @Test
        fun testMixedLiteralKeys() {
            // { [0]: Bool, [`key2`]: Bool }
            assertEquals(
                expected = StructType(
                    entries = mapOf(
                        IntLiteralType.of(0) to BoolType,
                        SymbolType.of("key2") to IntCollectiveType,
                    ),
                ), actual = Expression.parse(
                    source = """
                        {
                            [0] = value1,
                            [`key2`] = value2,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to IntCollectiveType,
                        ),
                    ).asStaticScope(),
                )
            )
        }

        @Test
        fun testInconsistentCollectiveKeys() {
            assertThrows<InconsistentKeysError> {
                Expression.parse(
                    source = """
                        {
                            [key1] = value1,
                            [key2] = value2,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("key2") to IntCollectiveType,
                            Symbol.of("value1") to BoolType,
                            Symbol.of("value2") to BoolType,
                        ),
                    ).asStaticScope(),
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
                            [key1] = value1,
                        }
                    """.trimIndent(),
                ).inferType(
                    scope = FixedStaticValueScope(
                        entries = mapOf(
                            Symbol.of("key1") to BoolType,
                            Symbol.of("value1") to UndefinedType,
                        ),
                    ).asStaticScope(),
                )
            }
        }
    }
}
