package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.ArrayTable
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationResult
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AbstractionTests {
    class TypeCheckingTests {
        @Test
        fun testDeclaredImageType() {
            val term = ExpressionSourceTerm.parse(
                source = "^[a: Int] -> Int => a + 3",
            ) as AbstractionSourceTerm

            val abstraction = Abstraction.build(
                outerDeclarationScope = BuiltinScope,
                term = term,
            )

            assertEquals(
                expected = IntCollectiveType,
                actual = abstraction.declaredImageType?.value,
            )
        }

        class InferredTypeTests {
            @Test
            fun testInferredFromValue() {
                val term = ExpressionSourceTerm.parse(
                    source = "^[a: Int] => 2 + 3",
                ) as AbstractionSourceTerm

                val abstraction = Abstraction.build(
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<IntType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testInferredFromDeclaration() {
                val term = ExpressionSourceTerm.parse(
                    source = "^[a: Int] -> Bool => 3 + 4",
                ) as AbstractionSourceTerm

                val abstraction = Abstraction.build(
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<BoolType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testInferredFromArguments() {
                val term = ExpressionSourceTerm.parse(
                    source = "^[a: Int] => a",
                ) as AbstractionSourceTerm

                val abstraction = Abstraction.build(
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertIs<IntType>(
                    value = inferredType.imageType,
                )
            }

            @Test
            fun testDeclaredFromGenericArguments() {
                val term = ExpressionSourceTerm.parse(
                    source = "![e] ^[a: e] -> e => a",
                ) as AbstractionSourceTerm

                val abstraction = Abstraction.build(
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertEquals(
                    expected = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("a"),
                                type = TypeVariable(
                                    formula = Formula.of("e"),
                                ),
                            ),
                        ),
                    ),
                    actual = inferredType.argumentType,
                )

                assertIs<TypeVariable>(
                    value = inferredType.imageType,
                )
            }
        }
    }

    class EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstraction = Abstraction.build(
                outerDeclarationScope = BuiltinScope, term = ExpressionSourceTerm.parse(
                    source = "^[n: Int, m: Int] => n * m",
                ) as AbstractionSourceTerm
            )

            val result = assertIs<EvaluationResult<Value>>(
                abstraction.bind(
                    scope = BuiltinScope,
                ).evaluateInitial(),
            )

            val closure = result.value

            assertIs<FunctionValue>(closure)

            val callResult = assertIs<EvaluationResult<Value>>(
                closure.apply(
                    argument = ArrayTable(
                        elements = listOf(
                            IntValue(2),
                            IntValue(3),
                        ),
                    ),
                ).evaluateInitial(),
            )

            assertEquals(
                expected = IntValue(6),
                actual = callResult.value,
            )
        }
    }
}
