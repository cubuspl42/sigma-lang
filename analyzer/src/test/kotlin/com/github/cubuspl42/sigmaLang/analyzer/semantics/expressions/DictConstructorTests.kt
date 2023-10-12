package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.Arbitrary
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals

class DictConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testSingleEntry() {
            val dictLiteral = DictConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
            ).resolved

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesCompatibleEntries() {
            val dictLiteral = DictConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = BoolType,
                        ),
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
            ).resolved

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleKeys() {
            val dictLiteral = DictConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = BoolType,
                        ),
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
            ).resolved

            assertEquals(
                expected = setOf(
                    DictConstructor.InconsistentKeyTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = dictLiteral.directErrors,
            )

            assertEquals(
                expected = DictType(
                    keyType = IllType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesIncompatibleValues() {
            val dictLiteral = DictConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            type = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = Arbitrary.unorderedTupleType,
                        ),
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
            ).resolved

            assertEquals(
                expected = setOf(
                    DictConstructor.InconsistentValueTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    ),
                ),
                actual = dictLiteral.directErrors,
            )

            assertEquals(
                expected = DictType(
                    keyType = IntCollectiveType,
                    valueType = IllType,
                ),
                actual = dictLiteral.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntriesNonPrimitiveKey() {
            val keyType = Arbitrary.unorderedTupleType

            val dictLiteral = DictConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            type = keyType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = """
                        {
                            [key1]: value1,
                        }
                    """.trimIndent(),
                ) as DictConstructorSourceTerm,
            ).resolved

            assertEquals(
                expected = setOf(
                    DictConstructor.NonPrimitiveKeyTypeError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        keyType = keyType,
                    ),
                ),
                actual = dictLiteral.directErrors,
            )

            assertEquals(
                expected = DictType(
                    keyType = IllType,
                    valueType = BoolType,
                ),
                actual = dictLiteral.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }
}
