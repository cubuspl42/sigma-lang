package sigma.semantics.expressions

import sigma.Arbitrary
import sigma.evaluation.values.Symbol
import sigma.semantics.types.BoolType
import sigma.semantics.types.DictType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.DictConstructorTerm
import sigma.syntax.expressions.ExpressionTerm
import utils.FakeStaticBlock
import utils.FakeValueDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals

class DictConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            val dictLiteral = DictConstructor.build(
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("key2"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("key2"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("key2"),
                        type = IntCollectiveType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value2"),
                        type = Arbitrary.unorderedTupleType,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("key1"),
                        type = keyType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
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
