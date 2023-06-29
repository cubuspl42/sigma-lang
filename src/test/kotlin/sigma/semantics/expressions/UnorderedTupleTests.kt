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
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm.DuplicatedNameError
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleTests {
    object TypeCheckingTests {
        @Test
        fun testEmpty() {
            val term = ExpressionTerm.parse(
                source = "{}",
            ) as UnorderedTupleConstructorTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
                typeScope = BuiltinTypeScope,
                declarationScope = DeclarationScope.Empty,
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
            val term = ExpressionTerm.parse(
                source = """
                    {
                        key1: value1,
                        key2: value2,
                    }
                """.trimIndent(),
            ) as UnorderedTupleConstructorTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
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
                actual = unorderedTupleConstructor.inferredType.value,
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
            ) as UnorderedTupleConstructorTerm

            val unorderedTupleConstructor = UnorderedTupleConstructor.build(
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
