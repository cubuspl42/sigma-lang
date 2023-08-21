package sigma.semantics.expressions

import sigma.evaluation.values.Symbol
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorSourceTerm
import utils.FakeStaticBlock
import utils.FakeValueDeclaration
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
                declarationScope = StaticScope.Empty,
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
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
                declarationScope = FakeStaticBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("value1"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
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
