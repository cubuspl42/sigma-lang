package sigma.semantics.expressions

import sigma.Arbitrary
import sigma.semantics.BuiltinTypeScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.DictConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals

object DictConstructorTests {
    object TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            val dictLiteral = DictConstructor.build(
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
                ) as DictConstructorTerm,
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
            val dictLiteral = DictConstructor.build(
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
                ) as DictConstructorTerm,
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
            val dictLiteral = DictConstructor.build(
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
                ) as DictConstructorTerm,
            )

            assertEquals(
                expected = setOf(
                    DictConstructor.InconsistentKeyTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = dictLiteral.errors,
            )

            assertEquals(
                expected = IllType,
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleValues() {
            val dictLiteral = DictConstructor.build(
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
                ) as DictConstructorTerm,
            )

            assertEquals(
                expected = setOf(
                    DictConstructor.InconsistentValueTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = dictLiteral.errors,
            )

            assertEquals(
                expected = IllType,
                actual = dictLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntriesNonPrimitiveKey() {
            val keyType = Arbitrary.unorderedTupleType

            val dictLiteral = DictConstructor.build(
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
                ) as DictConstructorTerm,
            )

            assertEquals(
                expected = setOf(
                    DictConstructor.NonPrimitiveKeyTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        keyType = keyType,
                    ),
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
