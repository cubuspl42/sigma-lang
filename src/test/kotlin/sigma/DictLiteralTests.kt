package sigma

import org.junit.jupiter.api.assertThrows
import sigma.syntax.expressions.DictLiteralTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IntCollectiveType
import sigma.syntax.expressions.ExpressionTerm
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object DictLiteralTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        DictLiteralTerm.Association(
                            key = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referee = Symbol.of("foo"),
                            ),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referee = Symbol.of("value1"),
                            ),
                        ),
                        DictLiteralTerm.Association(
                            key = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz"),
                            ),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 23),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{[foo]: value1, [baz]: value2}"),
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
                actual = ExpressionTerm.parse(
                    source = """
                            {
                                [key1]: value1,
                            }
                        """.trimIndent(),
                ).determineType(
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
                actual = ExpressionTerm.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).determineType(
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
            assertThrows<DictLiteralTerm.InconsistentKeyTypesError> {
                ExpressionTerm.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).determineType(
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
            assertThrows<DictLiteralTerm.InconsistentValueTypesError> {
                ExpressionTerm.parse(
                    source = """
                            {
                                [key1]: value1,
                                [key2]: value2,
                            }
                        """.trimIndent(),
                ).determineType(
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
            assertThrows<DictLiteralTerm.NonPrimitiveKeyTypeError> {
                ExpressionTerm.parse(
                    source = """
                            {
                                [key1]: value1,
                            }
                        """.trimIndent(),
                ).determineType(
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
