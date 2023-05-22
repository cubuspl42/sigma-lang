package sigma.semantics.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.semantics.DeclarationScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.OrderedTupleLiteralTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import sigma.evaluation.values.tables.FixedScope
import sigma.evaluation.values.tables.Scope
import utils.FakeDeclarationScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderedTupleLiteralTests {
    object TypeInferenceTests {
        @Test
        fun testEmpty() {
            val term = ExpressionTerm.parse(
                source = "[]",
            ) as OrderedTupleLiteralTerm

            val tupleLiteral = OrderedTupleLiteral.build(
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
            ) as OrderedTupleLiteralTerm

            val tupleLiteral = OrderedTupleLiteral.build(
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
