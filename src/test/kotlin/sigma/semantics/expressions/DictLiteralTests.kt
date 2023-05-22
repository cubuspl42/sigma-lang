package sigma.semantics.expressions

import org.junit.jupiter.api.assertThrows
import sigma.Arbitrary
import sigma.BuiltinTypeScope
import sigma.syntax.expressions.DictLiteralTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IntCollectiveType
import sigma.syntax.expressions.ExpressionTerm
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.Symbol
import sigma.semantics.types.IllType
import sigma.syntax.SourceLocation
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

object DictLiteralTests {
    object TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            val dictLiteral = DictLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "key1" to IntCollectiveType,
                        "value1" to BoolType,
                    ),

                    ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictLiteralTerm,
            )

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesCompatibleEntries() {
            val dictLiteral = DictLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "key1" to IntCollectiveType,
                        "value1" to BoolType,
                        "key2" to IntCollectiveType,
                        "value2" to BoolType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictLiteralTerm,
            )

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleKeys() {
            val dictLiteral = DictLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "key1" to IntCollectiveType,
                        "value1" to BoolType,
                        "key2" to BoolType,
                        "value2" to BoolType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictLiteralTerm,
            )

            assertEquals(
                expected = dictLiteral.errors,
                actual = setOf(
                    DictLiteral.InconsistentKeyTypeError,
                )
            )

            assertEquals(
                expected = IllType,
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleValues() {
            val dictLiteral = DictLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                       "key1" to IntCollectiveType,
                       "value1" to BoolType,
                       "key2" to IntCollectiveType,
                       "value2" to Arbitrary.unorderedTupleType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictLiteralTerm,
            )

            assertEquals(
                expected = dictLiteral.errors,
                actual = setOf(
                    DictLiteral.InconsistentValueTypeError,
                )
            )

            assertEquals(
                expected = IllType,
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesNonPrimitiveKey() {
            val keyType = Arbitrary.unorderedTupleType

            val dictLiteral = DictLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "key1" to keyType,
                        "value1" to BoolType,
                    ),
                ),
                term = ExpressionTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictLiteralTerm,
            )

            assertEquals(
                expected = setOf(
                    DictLiteral.NonPrimitiveKeyTypeError(keyType = keyType),
                ),
                actual = dictLiteral.errors,
            )

            assertEquals(
                expected = IllType,
                actual = dictLiteral.inferredType.value,
            )
        }
    }
}
