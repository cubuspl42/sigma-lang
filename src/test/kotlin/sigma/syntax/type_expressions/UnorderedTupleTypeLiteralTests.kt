package sigma.syntax.type_expressions

import sigma.TypeScope
import sigma.syntax.metaExpressions.MetaExpressionTerm
import sigma.syntax.metaExpressions.MetaReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.metaExpressions.UnorderedTupleTypeLiteralTerm
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
            val expression = MetaExpressionTerm.parse(
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
            val expression = MetaExpressionTerm.parse(
                source = "{a: A, b: B, c: C}",
            )

            assertEquals(
                expected = UnorderedTupleTypeLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleTypeLiteralTerm.Entry(
                            name = Symbol.of("a"),
                            valueType = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("A"),
                            ),
                        ),
                        UnorderedTupleTypeLiteralTerm.Entry(
                            name = Symbol.of("b"),
                            valueType = MetaReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                referee = Symbol.of("B"),
                            ),
                        ),
                        UnorderedTupleTypeLiteralTerm.Entry(
                            name = Symbol.of("c"),
                            valueType = MetaReferenceTerm(
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
            val type = MetaExpressionTerm.parse(
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
            val type = MetaExpressionTerm.parse(
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
