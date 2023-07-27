package sigma.syntax.typeExpressions

import sigma.semantics.StaticScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.Symbol
import utils.FakeDeclarationBlock
import utils.FakeTypeEntityDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
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
            val expression = TypeExpressionTerm.parse(
                source = "^{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("a"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 5),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("b"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 11),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("c"),
                            valueType = TypeReferenceTerm(
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

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val type = TypeExpressionTerm.parse(
                source = "^{}",
            ).evaluate(
                declarationScope = StaticScope.Empty,
            )

            assertEquals(
                expected = UnorderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = TypeExpressionTerm.parse(
                source = "^{a: A, b: B, c: C}",
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
                    FakeTypeEntityDefinition(
                        name = Symbol.of("C"),
                        definedTypeEntity = IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = UnorderedTupleType(
                    valueTypeByName = mapOf(
                        Symbol.of("a") to BoolType,
                        Symbol.of("b") to IntCollectiveType,
                        Symbol.of("c") to IntCollectiveType,
                    ),
                ),
                actual = type,
            )
        }
    }
}
