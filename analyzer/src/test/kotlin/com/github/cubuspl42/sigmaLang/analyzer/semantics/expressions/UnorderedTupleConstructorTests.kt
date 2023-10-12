package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import utils.assertTypeIsEquivalent
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleConstructorTests {
    class TypeCheckingTests {
        @Test
        fun testEmpty() {
            val term = ExpressionSourceTerm.parse(
                source = "{}",
            ) as UnorderedTupleConstructorSourceTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
                context = Expression.BuildContext.Empty,
                term = term,
            ).resolved

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = emptyMap(),
                ),
                actual = unorderedTupleConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testMultipleEntries() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    {
                        key1: value1,
                        key2: value2,
                    }
                """.trimIndent(),
            ) as UnorderedTupleConstructorSourceTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key1") to BoolType,
                        Identifier.of("key2") to IntCollectiveType,
                    ),
                ),
                actual = unorderedTupleConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }

        @Test
        fun testDuplicatedName() {
            val term = ExpressionSourceTerm.parse(
                source = """
                    {
                        key1: value1,
                        key1: value2,
                    }
                """.trimIndent(),
            ) as UnorderedTupleConstructorSourceTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("value1"),
                            type = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("value2"),
                            type = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            assertEquals(
                expected = setOf(
                    UnorderedTupleConstructor.DuplicatedKeyError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        duplicatedKey = Identifier.of("key1"),
                    ),
                ),
                actual = unorderedTupleConstructor.directErrors,
            )

            assertTypeIsEquivalent(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Identifier.of("key1") to IllType,
                    ),
                ),
                actual = unorderedTupleConstructor.inferredTypeOrIllType.getOrCompute(),
            )
        }
    }
}
