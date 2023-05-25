package sigma.semantics.expressions

import org.junit.jupiter.api.assertThrows
import sigma.BuiltinTypeScope
import sigma.TypeScope
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.Symbol
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IllType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm.DuplicatedNameError
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleTests {
    object TypeCheckingTests {
        @Test
        fun testEmpty() {
            val term = ExpressionTerm.parse(
                source = "{}",
            ) as UnorderedTupleLiteralTerm

            val unorderedTupleLiteral = UnorderedTupleLiteral.build(
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = emptyMap(),
                ),
                actual = unorderedTupleLiteral.inferredType.value,
            )
        }

        @Test
        fun testMultipleEntries() {
            val term = ExpressionTerm.parse(
                source = """
                    {
                        key1: value1,
                        key2: value2,
                    }
                """.trimIndent(),
            ) as UnorderedTupleLiteralTerm

            val unorderedTupleLiteral = UnorderedTupleLiteral.build(
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
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("key1") to BoolType,
                        Symbol.of("key2") to IntCollectiveType,
                    ),
                ),
                actual = unorderedTupleLiteral.inferredType.value,
            )
        }

        @Test
        fun testDuplicatedName() {
            val term = ExpressionTerm.parse(
                source = """
                    {
                        key1: value1,
                        key1: value2,
                    }
                """.trimIndent(),
            ) as UnorderedTupleLiteralTerm

            val unorderedTupleLiteral = UnorderedTupleLiteral.build(
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
                    UnorderedTupleLiteral.DuplicatedKeyError(
                        duplicatedKey = Symbol.of("key1"),
                    ),
                ),
                actual = unorderedTupleLiteral.errors,
            )

            assertEquals(
                expected = IllType,
                actual = unorderedTupleLiteral.inferredType.value,
            )
        }
    }
}
