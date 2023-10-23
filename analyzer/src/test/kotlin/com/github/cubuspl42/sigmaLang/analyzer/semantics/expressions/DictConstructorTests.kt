package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.Arbitrary
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.DictType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.DictConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import utils.FakeStaticScope
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            declaredType = BoolType,
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            declaredType = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            declaredType = BoolType,
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            declaredType = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            declaredType = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            declaredType = BoolType,
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            declaredType = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("key2"),
                            declaredType = IntCollectiveType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            declaredType = Arbitrary.unorderedTupleType,
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
                    outerScope = FakeStaticScope.of(
                        FakeUserDeclaration(
                            name = Identifier.of("key1"),
                            declaredType = keyType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            declaredType = BoolType,
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
