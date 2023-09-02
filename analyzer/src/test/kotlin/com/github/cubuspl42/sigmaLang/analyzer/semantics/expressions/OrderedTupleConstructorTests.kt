package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.FixedDynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorSourceTerm
import utils.FakeStaticBlock
import utils.FakeValueDeclaration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OrderedTupleConstructorTests {
    class TypeInferenceTests {
        @Test
        fun testEmpty() {
            val term = ExpressionSourceTerm.parse(
                source = "[]",
            ) as OrderedTupleConstructorSourceTerm

            val tupleLiteral = OrderedTupleConstructor.build(
                outerScope = StaticScope.Empty,
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
            val term = ExpressionSourceTerm.parse(
                source = "[a, b]",
            ) as OrderedTupleConstructorSourceTerm

            val tupleLiteral = OrderedTupleConstructor.build(
                outerScope = FakeStaticBlock.of(
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

    class EvaluationTests {
        @Test
        fun testEmpty() {
            val tupleConstructor = OrderedTupleConstructor.build(
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "[]",
                ) as OrderedTupleConstructorSourceTerm,
            )

            val value = tupleConstructor.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = DynamicScope.Empty,
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
                outerScope = StaticScope.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "[a, b]",
                ) as OrderedTupleConstructorSourceTerm,
            )

            val value = tupleConstructor.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = FixedDynamicScope(
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
