package sigma.syntax.type_expressions

import sigma.TypeScope
import sigma.syntax.typeExpressions.TypeExpressionTerm
import sigma.TypeReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.UnorderedTupleTypeLiteralTerm
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.evaluation.values.FixedTypeScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleTypeLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = TypeExpressionTerm.parse(
                source = "{}",
            )

            assertEquals(
                expected = UnorderedTupleTypeLiteralTerm(
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
                expected = UnorderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeLiteralTerm.Entry(
                            name = Symbol.of("a"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeLiteralTerm.Entry(
                            name = Symbol.of("b"),
                            valueType = TypeReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeLiteralTerm.Entry(
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
                typeScope = TypeScope.Empty,
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
                typeScope = FixedTypeScope(
                    entries = mapOf(
                        Symbol.of("A") to BoolType,
                        Symbol.of("B") to IntCollectiveType,
                        Symbol.of("C") to IntCollectiveType,
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
