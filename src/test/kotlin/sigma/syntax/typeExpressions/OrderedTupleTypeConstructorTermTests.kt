package sigma.syntax.typeExpressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.OrderedTupleTypeConstructorTerm
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionTerm.parse(
                source = "^[]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleUnnamed() {
            val expression = ExpressionTerm.parse(
                source = "^[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referee = Symbol.of("A"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testAllUnnamed() {
            val expression = ExpressionTerm.parse(
                source = "^[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSomeNamed() {
            val expression = ExpressionTerm.parse(
                source = "^[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = Symbol.of("a"),
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = Symbol.of("c"),
                            type = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 14),
                                referee = Symbol.of("C"),
                            ),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object EvaluationTests {
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
