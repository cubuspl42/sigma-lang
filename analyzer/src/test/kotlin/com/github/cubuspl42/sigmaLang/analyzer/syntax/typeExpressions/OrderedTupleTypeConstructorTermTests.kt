package com.github.cubuspl42.sigmaLang.analyzer.syntax.typeExpressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.OrderedTupleTypeConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionSourceTerm.parse(
                source = "^[]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnnamed() {
            val expression = ExpressionSourceTerm.parse(
                source = "^[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = null,
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referredName = Symbol.of("A"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testAllUnnamed() {
            val expression = ExpressionSourceTerm.parse(
                source = "^[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = null,
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referredName = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = null,
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referredName = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = null,
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referredName = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSomeNamed() {
            val expression = ExpressionSourceTerm.parse(
                source = "^[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = Symbol.of("a"),
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referredName = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = null,
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referredName = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorSourceTerm.Element(
                            name = Symbol.of("c"),
                            type = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 14),
                                referredName = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }

    class EvaluationTests {
        @Test
        fun testEmpty() {
//            val type = ExpressionTerm.parse(
//                source = "^[]",
//            ).evaluate(
//                declarationScope = StaticScope.Empty,
//            )
//
//            assertEquals(
//                expected = OrderedTupleType.Empty,
//                actual = type,
//            )
        }

        @Test
        fun testNonEmpty() {
//            val type = ExpressionTerm.parse(
//                source = "^[a: A, B]",
//            ).evaluate(
//                declarationScope = FakeDeclarationBlock.of(
//                    FakeTypeEntityDefinition(
//                        name = Symbol.of("A"),
//                        definedTypeEntity = BoolType,
//                    ),
//                    FakeTypeEntityDefinition(
//                        name = Symbol.of("B"),
//                        definedTypeEntity = IntCollectiveType,
//                    ),
//                ),
//            )
//
//            assertEquals(
//                expected = OrderedTupleType(
//                    elements = listOf(
//                        OrderedTupleType.Element(
//                            name = Symbol.of("a"),
//                            type = BoolType,
//                        ),
//                        OrderedTupleType.Element(
//                            name = null,
//                            type = IntCollectiveType,
//                        ),
//                    )
//                ),
//                actual = type,
//            )
        }
    }
}
