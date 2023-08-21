package sigma.syntax.typeExpressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ExpressionSourceTerm
import sigma.syntax.expressions.ReferenceSourceTerm
import sigma.syntax.expressions.UnorderedTupleConstructorSourceTerm
import sigma.syntax.expressions.UnorderedTupleTypeConstructorSourceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionSourceTerm.parse(
                source = "^{}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testNonEmpty() {
            val expression = ExpressionSourceTerm.parse(
                source = "^{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("a"),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("b"),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("c"),
                            value = ReferenceSourceTerm(
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
