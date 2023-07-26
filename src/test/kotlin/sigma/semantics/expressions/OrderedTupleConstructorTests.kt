package sigma.semantics.expressions

import sigma.evaluation.scope.FixedScope
import sigma.evaluation.scope.Scope
import sigma.evaluation.values.BoolValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.ArrayTable
import sigma.evaluation.values.DictValue
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.OrderedTupleConstructorTerm
import utils.FakeDeclarationBlock
import utils.FakeValueDeclaration
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
                declarationScope = StaticScope.Empty,
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
                declarationScope = FakeDeclarationBlock.of(
                    FakeValueDeclaration(
                        name = Symbol.of("a"),
                        type = BoolType,
                    ),
                    FakeValueDeclaration(
                        name = Symbol.of("b"),
                        type = IntCollectiveType,
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
                declarationScope = StaticScope.Empty,
                term = ExpressionTerm.parse(
                    source = "[]",
                ) as OrderedTupleConstructorTerm,
            )

            val value = tupleConstructor.evaluate(
                scope = Scope.Empty,
            )

            assertIs<DictValue>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = emptyList(),
                ).entries,
                actual = value.entries,
            )
        }

        @Test
        fun testNonEmpty() {
            val tupleConstructor = OrderedTupleConstructor.build(
                declarationScope = StaticScope.Empty,
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
            )

            assertIs<DictValue>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ).entries,
                actual = value.entries,
            )
        }
    }
}
