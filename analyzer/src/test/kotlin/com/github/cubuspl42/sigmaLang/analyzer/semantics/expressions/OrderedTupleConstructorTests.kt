package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleConstructorSourceTerm
import utils.FakeDefinition
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
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
                context = Expression.BuildContext.Empty,
                term = term,
            ).resolved

            val type = assertIs<OrderedTupleType>(
                value = tupleLiteral.inferredTypeOrIllType.getOrCompute(),
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
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeUserDeclaration(
                            name = Identifier.of("a"),
                            annotatedType = BoolType,
                        ),
                        FakeUserDeclaration(
                            name = Identifier.of("b"),
                            annotatedType = IntCollectiveType,
                        ),
                    ),
                ),
                term = term,
            ).resolved

            val type = assertIs<OrderedTupleType>(
                value = tupleLiteral.inferredTypeOrIllType.getOrCompute(),
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
                context = Expression.BuildContext.Empty,
                term = ExpressionSourceTerm.parse(
                    source = "[]",
                ) as OrderedTupleConstructorSourceTerm,
            ).resolved

            val value = tupleConstructor.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = DynamicScope.Empty,
            )

            assertIs<DictValue>(value)

            assertEquals(
                expected = emptyMap(),
                actual = value.entries,
            )
        }

        @Test
        fun testNonEmpty() {
            val tupleConstructor = OrderedTupleConstructor.build(
                context = Expression.BuildContext(
                    outerMetaScope = StaticScope.Empty,
                    outerScope = FakeStaticBlock.of(
                        FakeDefinition(
                            name = Identifier.of("a"),
                            type = NeverType,
                            value = BoolValue(false),
                        ),
                        FakeDefinition(
                            name = Identifier.of("b"),
                            type = NeverType,
                            value = IntValue(1),
                        ),
                    ),
                ),
                term = ExpressionSourceTerm.parse(
                    source = "[a, b]",
                ) as OrderedTupleConstructorSourceTerm,
            ).resolved

            val value = tupleConstructor.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = DynamicScope.Empty,
            )

            assertIs<DictValue>(value)

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ).valueEntries,
                actual = value.valueEntries,
            )
        }
    }
}
