package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.BuiltinScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

fun <T> assertMatchesEachOnce(
    actual: Collection<T>,
    /**
     * Assertion blocks keyed by labels
     */
    blocks: Map<String, (T) -> Unit>,
) {
    val actualSize = actual.size
    val expectedSize = blocks.size

    if (actualSize != expectedSize) {
        throw AssertionError("Unexpected collection size. Actual: ${actualSize}, expected: $expectedSize")
    }

    blocks.forEach { (name, block) ->
        val matchingElements = actual.filter {
            try {
                block(it)
                true
            } catch (e: AssertionError) {
                false
            }
        }

        if (matchingElements.isEmpty()) {
            throw AssertionError("No elements matched block '$name'")
        }

        if (matchingElements.size > 1) {
            throw AssertionError("More than one element matched block '$name': $matchingElements")
        }
    }
}

fun <T> assertMatchesEachInOrder(
    actual: Collection<T>,
    /**
     * Assertion blocks keyed by labels
     */
    blocks: List<(T) -> Unit>,
) {
    val actualSize = actual.size
    val expectedSize = blocks.size

    if (actualSize != expectedSize) {
        throw AssertionError("Unexpected collection size. Actual: ${actualSize}, expected: $expectedSize")
    }

    actual.zip(blocks).forEachIndexed { index, (element, block) ->
        try {
            block(element)
        } catch (e: AssertionError) {
            throw AssertionError("At index $index: ${e.message}")
        }
    }
}

class UnionTypeConstructorTests {
    @Test
    fun testBuildFlat() {
        val term = ExpressionSourceTerm.parse(
            source = "A | ^[B]",
        ) as UnionTypeConstructorTerm

        val unionTypeConstructor = UnionTypeConstructor.build(
            context = Expression.BuildContext.Empty,
            term = term,
        )

        val types = unionTypeConstructor.types

        assertMatchesEachOnce(
            actual = types,
            blocks = mapOf(
                "A" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredName,
                        expected = Symbol.of("A"),
                    )
                },
                "^[B]" to { expression ->
                    assertIs<OrderedTupleTypeConstructor>(expression)

                    assertMatchesEachInOrder(
                        actual = expression.elements,
                        blocks = listOf {
                            val reference = assertIs<Reference>(it.type)

                            assertEquals(
                                actual = reference.referredName,
                                expected = Symbol.of("B"),
                            )
                        },
                    )
                },
            ),
        )
    }

    @Test
    fun testBuildNested() {
        val term = ExpressionSourceTerm.parse(
            source = "A | B | D | C",
        ) as UnionTypeConstructorTerm

        val unionTypeConstructor = UnionTypeConstructor.build(
            context = Expression.BuildContext.Empty,
            term = term,
        )

        val types = unionTypeConstructor.types

        assertMatchesEachOnce(
            actual = types,
            blocks = mapOf(
                "A" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredName,
                        expected = Symbol.of("A"),
                    )
                },
                "B" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredName,
                        expected = Symbol.of("B"),
                    )
                },
                "C" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredName,
                        expected = Symbol.of("C"),
                    )
                },
                "D" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredName,
                        expected = Symbol.of("D"),
                    )
                },
            ),
        )
    }

    class EvaluationTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "Int | Bool | ^[Bool]",
            ) as UnionTypeConstructorTerm

            val unionTypeConstructor = UnionTypeConstructor.build(
                context = Expression.BuildContext.Builtin,
                term = term,
            )

            val evaluatedTypeValue = assertIs<TypeValue<*>>(
                unionTypeConstructor.evaluateValue(
                    context = EvaluationContext.Initial,
                    dynamicScope = BuiltinScope,
                )
            )

            val unionType = assertIs<UnionType>(evaluatedTypeValue.asType)

            assertMatchesEachOnce(
                actual = unionType.memberTypes,
                blocks = mapOf(
                    "Int" to { type ->
                        assertEquals(
                            actual = type,
                            expected = IntCollectiveType,
                        )
                    },
                    "Bool" to { type ->
                        assertEquals(
                            actual = type,
                            expected = BoolType,
                        )
                    },
                    "^[Bool]" to { type ->
                        assertEquals(
                            actual = type,
                            expected = OrderedTupleType(
                                elements = listOf(
                                    OrderedTupleType.Element(
                                        name = null,
                                        type = BoolType,
                                    ),
                                ),
                            ),
                        )
                    },
                ),
            )
        }
    }
}
