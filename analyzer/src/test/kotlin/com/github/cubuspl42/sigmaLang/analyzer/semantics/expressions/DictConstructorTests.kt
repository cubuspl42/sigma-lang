package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.Arbitrary
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.FakeStaticBlock
import utils.FakeDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals

class DictConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            val dictLiteral = DictConstructor.build(
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("key2"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("key2"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value2"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("key1"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("key2"),
                        type = IntCollectiveType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value2"),
                        type = Arbitrary.unorderedTupleType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                            [key2]: value2,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
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
                outerScope = FakeStaticBlock.of(
                    FakeDeclaration(
                        name = Symbol.of("key1"),
                        type = keyType,
                    ),
                    FakeDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
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
