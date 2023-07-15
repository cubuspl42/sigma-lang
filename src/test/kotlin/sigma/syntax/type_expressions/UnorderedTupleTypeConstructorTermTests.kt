package sigma.syntax.type_expressions

import sigma.semantics.DeclarationScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.syntax.typeExpressions.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.UnorderedTupleTypeConstructorTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.Symbol
import utils.FakeDeclarationBlock
import utils.FakeTypeDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeConstructorTermTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
                source = "{}",
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
                source = "{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("a"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("b"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeConstructorTerm.Entry(
                            name = Symbol.of("c"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 16),
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
                source = "{}",
            ).evaluate(
                declarationScope = DeclarationScope.Empty,
            )

            assertEquals(
                expected = UnorderedTupleType.Empty,
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = TypeExpressionTerm.parse(
                source = "{a: A, b: B, c: C}",
            ).evaluate(
                declarationScope = FakeDeclarationBlock.of(
                    FakeTypeDefinition(
                        name = Symbol.of("A"),
                        definedType = BoolType,
                    ),
                    FakeTypeDefinition(
                        name = Symbol.of("B"),
                        definedType = IntCollectiveType,
                    ),
                    FakeTypeDefinition(
                        name = Symbol.of("C"),
                        definedType = IntCollectiveType,
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
