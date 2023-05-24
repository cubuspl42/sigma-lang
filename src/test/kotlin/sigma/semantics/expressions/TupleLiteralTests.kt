package sigma.semantics.expressions

import sigma.TypeScope
import sigma.evaluation.values.Symbol
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.TupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.TupleLiteralTerm
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TupleLiteralTests {
    object TypeInferenceTests {
        @Test
        fun testEmpty() {
            val term = ExpressionTerm.parse(
                source = "[]",
            ) as TupleLiteralTerm

            val tupleLiteral = TupleLiteral.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            val type = assertIs<TupleType>(
                value = tupleLiteral.inferredType.value,
            )

            assertEquals(
                expected = TupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testMultipleOrderedEntries() {
            val term = ExpressionTerm.parse(
                source = "[a, b]",
            ) as TupleLiteralTerm

            val tupleLiteral = TupleLiteral.build(
                typeScope = TypeScope.Empty,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "a" to BoolType,
                        "b" to IntCollectiveType,
                    ),
                ),
                term = term,
            )

            val type = assertIs<TupleType>(
                value = tupleLiteral.inferredType.value,
            )

            assertEquals(
                expected = TupleType.ordered(
                    TupleType.OrderedEntry(
                        index = 0,
                        name = null,
                        type = BoolType,
                    ),
                    TupleType.OrderedEntry(
                        index = 1,
                        name = null,
                        type = IntCollectiveType,
                    ),
                ),
                actual = type,
            )
        }

        @Test
        fun testMultipleUnorderedEntries() {
            val term = ExpressionTerm.parse(
                source = """
                    [
                        key1: value1,
                        key2: value2,
                    ]
                """.trimIndent(),
            ) as TupleLiteralTerm

            val tupleLiteral = TupleLiteral.build(
                typeScope = TypeScope.Empty,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "value1" to BoolType,
                        "value2" to IntCollectiveType,
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = TupleType.unordered(
                    TupleType.UnorderedEntry(
                        name = Symbol.of("key1"),
                        type = BoolType,
                    ),
                    TupleType.UnorderedEntry(
                        name = Symbol.of("key2"),
                        type = IntCollectiveType,
                    ),
                ),
                actual = tupleLiteral.inferredType.value,
            )
        }

        @Test
        fun testDuplicatedTargetName() {
            val term = ExpressionTerm.parse(
                source = """
                    [
                        key1: value1,
                        key1: value2,
                    ]
                """.trimIndent(),
            ) as TupleLiteralTerm

            val tupleLiteral = TupleLiteral.build(
                typeScope = TypeScope.Empty,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "value1" to BoolType,
                        "value2" to IntCollectiveType,
                    ),
                ),
                term = term,
            )

            assertEquals(
                expected = setOf(
                    TupleLiteral.DuplicatedKeyError(
                        duplicatedKey = Symbol.of("key1"),
                    ),
                ),
                actual = tupleLiteral.errors,
            )

            assertEquals(
                expected = IllType,
                actual = tupleLiteral.inferredType.value,
            )
        }
    }
}
