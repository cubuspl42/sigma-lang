package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionTerm.parse(
                source = "^{}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testNonEmpty() {
            val expression = ExpressionTerm.parse(
                source = "^{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            name = Symbol.of("a"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            name = Symbol.of("b"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleConstructorTerm.Entry(
                            name = Symbol.of("c"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("C"),
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
//                source = "^{}",
//            ).evaluate(
//                declarationScope = StaticScope.Empty,
//            )
//
//            assertEquals(
//                expected = UnorderedTupleType.Empty,
//                actual = type,
//            )
        }

        @Test
        fun testNonEmpty() {
//            val type = ExpressionTerm.parse(
//                source = "^{a: A, b: B, c: C}",
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
//                    FakeTypeEntityDefinition(
//                        name = Symbol.of("C"),
//                        definedTypeEntity = IntCollectiveType,
//                    ),
//                ),
//            )
//
//            assertEquals(
//                expected = UnorderedTupleType(
//                    valueTypeByName = mapOf(
//                        Symbol.of("a") to BoolType,
//                        Symbol.of("b") to IntCollectiveType,
//                        Symbol.of("c") to IntCollectiveType,
//                    ),
//                ),
//                actual = type,
//            )
        }
    }
}
