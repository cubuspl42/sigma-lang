package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
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
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTests {
    class TypeCheckingTests {
        @Test
        fun testEmpty() {
            val term = ExpressionSourceTerm.parse(
                source = "{}",
            ) as UnorderedTupleConstructorSourceTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
                outerScope = StaticScope.Empty,
                term = term,
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = emptyMap(),
                ),
                actual = unorderedTupleConstructor.inferredType.value,
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
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeUserDeclaration(
                        name = Symbol.of("value2"),
                        type = IntCollectiveType,
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key1") to BoolType,
                        Symbol.of("key2") to IntCollectiveType,
                    ),
                ),
                actual = unorderedTupleConstructor.inferredType.value,
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
                outerScope = FakeStaticBlock.of(
                    FakeUserDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeUserDeclaration(
                        name = Symbol.of("value2"),
                        type = IntCollectiveType,
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = setOf(
                    UnorderedTupleConstructor.DuplicatedKeyError(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        duplicatedKey = Symbol.of("key1"),
                    ),
                ),
                actual = unorderedTupleConstructor.errors,
            )

            assertEquals(
                expected = IllType,
                actual = unorderedTupleConstructor.inferredType.value,
            )
        }
    }
}
