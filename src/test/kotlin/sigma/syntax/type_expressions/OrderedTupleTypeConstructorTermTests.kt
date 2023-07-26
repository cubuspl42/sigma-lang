package sigma.syntax.type_expressions

import sigma.evaluation.values.Symbol
import sigma.semantics.StaticScope
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.OrderedTupleTypeConstructorTerm
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.syntax.typeExpressions.TypeReferenceTerm
import utils.FakeDeclarationBlock
import utils.FakeTypeEntityDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
                source = "[]",
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
            val expression = TypeExpressionTerm.parse(
                source = "[A]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
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
            val expression = TypeExpressionTerm.parse(
                source = "[A, B, C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 1),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
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
            val expression = TypeExpressionTerm.parse(
                source = "[a: A, B, c: C]",
            )

            assertEquals(
                expected = OrderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    elements = listOf(
                        OrderedTupleTypeConstructorTerm.Element(
                            name = Symbol.of("a"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = null,
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        OrderedTupleTypeConstructorTerm.Element(
                            name = Symbol.of("c"),
                            type = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 13),
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
            val type = TypeExpressionTerm.parse(
                source = "[]",
            ).evaluate(
                declarationScope = StaticScope.Empty,
            )

            assertEquals(
                expected = OrderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = TypeExpressionTerm.parse(
                source = "[a: A, B]",
            ).evaluate(
                declarationScope = FakeDeclarationBlock.of(
                    FakeTypeEntityDefinition(
                        name = Symbol.of("A"),
                        definedTypeEntity = BoolType,
                    ),
                    FakeTypeEntityDefinition(
                        name = Symbol.of("B"),
                        definedTypeEntity = IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(
                            name = Symbol.of("a"),
                            type = BoolType,
                        ),
                        OrderedTupleType.Element(
                            name = null,
                            type = IntCollectiveType,
                        ),
                    )
                ),
                actual = type,
            )
        }
    }
}
