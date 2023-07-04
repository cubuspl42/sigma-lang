package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.ArrayTable
import sigma.evaluation.values.tables.DictTable
import sigma.semantics.TypeScope
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

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val tupleConstructor = OrderedTupleConstructor.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = ExpressionTerm.parse(
                    source = "[]",
                ) as OrderedTupleConstructorTerm,
            )

            val value = tupleConstructor.evaluate(
                scope = Scope.Empty,
            ).toEvaluatedValue

            assertIs<DictTable>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = emptyList(),
                ).evaluatedEntries,
                actual = value.evaluatedEntries,
            )
        }

        @Test
        fun testNonEmpty() {
            val tupleConstructor = OrderedTupleConstructor.build(
                typeScope = TypeScope.Empty,
                declarationScope = DeclarationScope.Empty,
                term = ExpressionTerm.parse(
                    source = "[a, b]",
                ) as OrderedTupleConstructorTerm,
            )

            val value = tupleConstructor.evaluate(
                scope = FixedScope(
                    entries = mapOf(
                        Symbol.of("a") to BoolValue(false),
                        Symbol.of("b") to IntValue(1),
                    ),
                ),
            ).toEvaluatedValue

            assertIs<DictTable>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ).evaluatedEntries,
                actual = value.evaluatedEntries,
            )
        }
    }
}