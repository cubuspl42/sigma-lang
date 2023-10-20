package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.TypeValue
import com.github.cubuspl42.sigmaLang.analyzer.semantics.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntCollectiveType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnionTypeConstructorTerm
import utils.CollectionMatchers
import utils.FakeStaticBlock
import utils.FakeUserDeclaration
import utils.assertMatches
import utils.assertMatchesEachInOrder
import utils.assertMatchesEachOnce
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UnionTypeConstructorTests {
    @Test
    fun testBuildFlat() {
        val term = ExpressionSourceTerm.parse(
            source = "A | ^[B]",
        ) as UnionTypeConstructorTerm

        val aDeclaration = FakeUserDeclaration(
            name = Identifier.of("A"),
            annotatedType = TypeType,
        )

        val bDeclaration = FakeUserDeclaration(
            name = Identifier.of("B"),
            annotatedType = TypeType,
        )

        val unionTypeConstructor = UnionTypeConstructor.build(
            context = Expression.BuildContext(
                outerMetaScope = StaticScope.Empty,
                outerScope = FakeStaticBlock.of(
                    aDeclaration,
                    bDeclaration,
                ),
            ),
            term = term,
        ).resolved

        val types = unionTypeConstructor.types

        assertMatchesEachOnce(
            actual = types,
            blocks = mapOf(
                "A" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredDeclaration,
                        expected = aDeclaration,
                    )
                },
                "^[B]" to { expression ->
                    assertIs<OrderedTupleTypeConstructor>(expression)

                    assertMatchesEachInOrder(
                        actual = expression.elements,
                        blocks = listOf {
                            val reference = assertIs<Reference>(it.type)

                            assertEquals(
                                actual = reference.referredDeclaration,
                                expected = bDeclaration,
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

        val aDeclaration = FakeUserDeclaration(
            name = Identifier.of("A"),
            annotatedType = TypeType,
        )

        val bDeclaration = FakeUserDeclaration(
            name = Identifier.of("B"),
            annotatedType = TypeType,
        )

        val cDeclaration = FakeUserDeclaration(
            name = Identifier.of("C"),
            annotatedType = TypeType,
        )

        val dDeclaration = FakeUserDeclaration(
            name = Identifier.of("D"),
            annotatedType = TypeType,
        )

        val unionTypeConstructor = UnionTypeConstructor.build(
            context = Expression.BuildContext(
                outerMetaScope = StaticScope.Empty,
                outerScope = FakeStaticBlock.of(
                    aDeclaration,
                    bDeclaration,
                    cDeclaration,
                    dDeclaration,
                ),
            ),
            term = term,
        ).resolved

        val types = unionTypeConstructor.types

        assertMatchesEachOnce(
            actual = types,
            blocks = mapOf(
                "A" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredDeclaration,
                        expected = aDeclaration,
                    )
                },
                "B" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredDeclaration,
                        expected = bDeclaration,
                    )
                },
                "C" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredDeclaration,
                        expected = cDeclaration,
                    )
                },
                "D" to { expression ->
                    val reference = assertIs<Reference>(expression)

                    assertEquals(
                        actual = reference.referredDeclaration,
                        expected = dDeclaration,
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
            ).resolved

            val evaluatedTypeValue = assertIs<TypeValue<*>>(
                unionTypeConstructor.evaluateValue(
                    context = EvaluationContext.Initial,
                    dynamicScope = DynamicScope.Empty,
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
                            )
                        )
                    },
                ),
            )
        }
    }
}
