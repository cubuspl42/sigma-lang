package sigma.semantics.expressions

import sigma.evaluation.values.FunctionValue
import sigma.evaluation.values.IntValue
import sigma.semantics.BuiltinScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.ArrayTable
import sigma.semantics.types.BoolType
import sigma.semantics.types.FunctionType
import sigma.semantics.types.IntType
import sigma.semantics.types.OrderedTupleType
import sigma.semantics.types.TypeVariable
import sigma.syntax.expressions.AbstractionTerm
import sigma.syntax.expressions.ExpressionTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AbstractionTests {
    object TypeCheckingTests {
        object InferredTypeTests {
            @Test
            fun testInferredFromValue() {
                val term = ExpressionTerm.parse(
                    source = "^[a: Int] => 2 + 3",
                ) as AbstractionTerm

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
                val term = ExpressionTerm.parse(
                    source = "^[a: Int] -> Bool => 3 + 4",
                ) as AbstractionTerm

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
                val term = ExpressionTerm.parse(
                    source = "^[a: Int] => a",
                ) as AbstractionTerm

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
                val term = ExpressionTerm.parse(
                    source = "![e] ^[a: e] -> e => a",
                ) as AbstractionTerm

                val abstraction = Abstraction.build(
                    outerDeclarationScope = BuiltinScope,
                    term = term,
                )

                val inferredType = assertIs<FunctionType>(
                    value = abstraction.inferredType.value,
                )

                assertEquals(
                    expected = inferredType.argumentType, actual = OrderedTupleType(
                        elements = listOf(
                            OrderedTupleType.Element(
                                name = Symbol.of("a"),
                                type = TypeVariable(
                                    name = Symbol.of("e"),
                                ),
                            ),
                        ),
                    )
                )

                assertIs<TypeVariable>(
                    value = inferredType.imageType,
                )
            }
        }
    }

    object EvaluationTests {
        @Test
        fun testUnorderedArgumentTuple() {
            val abstraction = Abstraction.build(
                outerDeclarationScope = BuiltinScope, term = ExpressionTerm.parse(
                    source = "^[n: Int, m: Int] => n * m",
                ) as AbstractionTerm
            )

            val closure = abstraction.evaluate(
                context = EvaluationContext.Initial,
                scope = BuiltinScope,
            )

            assertIs<FunctionValue>(closure)

            assertEquals(
                expected = IntValue(6),
                actual = closure.apply(
                    context = EvaluationContext.Initial,
                    argument = ArrayTable(
                        elements = listOf(
                            IntValue(2),
                            IntValue(3),
                        ),
                    ),
                ),
            )
        }
    }
}
