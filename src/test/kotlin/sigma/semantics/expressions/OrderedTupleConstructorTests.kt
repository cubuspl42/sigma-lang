package sigma.semantics.expressions

import sigma.TypeScope
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.OrderedTupleConstructorTerm
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderedTupleConstructorTests {
    object TypeInferenceTests {
        @Test
        fun testEmpty() {
            val term = ExpressionTerm.parse(
                source = "[]",
            ) as OrderedTupleConstructorTerm

            val tupleLiteral = OrderedTupleConstructor.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = term,
            )

            val type = assertIs<OrderedTupleType>(
                value = tupleLiteral.inferredType.value,
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = emptyList(),
                ),
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val term = ExpressionTerm.parse(
                source = "[a, b]",
            ) as OrderedTupleConstructorTerm

            val tupleLiteral = OrderedTupleConstructor.build(
                typeScope = TypeScope.Empty,
                declarationScope = FakeDeclarationScope(
                    typeByName = mapOf(
                        "a" to BoolType,
                        "b" to IntCollectiveType,
                    ),
                ),
                term = term,
            )

            val type = assertIs<OrderedTupleType>(
                value = tupleLiteral.inferredType.value,
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(name = null, type = BoolType),
                        OrderedTupleType.Element(name = null, type = IntCollectiveType),
                    ),
                ),
                actual = type,
            )
        }
    }
}
