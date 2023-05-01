package sigma.syntax.expressions

import sigma.TypeScope
import sigma.SyntaxValueScope
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.OrderedTupleType
import sigma.values.BoolValue
import sigma.values.FixedStaticValueScope
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.tables.ArrayTable
import sigma.values.tables.FixedScope
import sigma.values.tables.Scope
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderedTupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            val expression = ExpressionTerm.parse(
                source = "[]",
            )

            assertEquals(
                expected = OrderedTupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    elements = emptyList(),
                ),
                actual = expression,
            )
        }

        @Test
        fun testSingleElement() {
            val expression = ExpressionTerm.parse(
                source = "[a]",
            )

            assertEquals(
                expected = OrderedTupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceTerm(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }

        @Test
        fun testMultipleElements() {
            val expression = ExpressionTerm.parse(
                source = "[a, b, c]",
            )

            assertEquals(
                expected = OrderedTupleLiteralTerm(
                    location = SourceLocation(1, 0),
                    elements = listOf(
                        ReferenceTerm(
                            location = SourceLocation(1, 1),
                            referee = Symbol.of("a"),
                        ),

                        ReferenceTerm(
                            location = SourceLocation(1, 4),
                            referee = Symbol.of("b"),
                        ),

                        ReferenceTerm(
                            location = SourceLocation(1, 7),
                            referee = Symbol.of("c"),
                        ),
                    ),
                ),
                actual = expression,
            )
        }
    }

    object TypeInferenceTests {
        @Test
        fun testEmpty() {
            val type = ExpressionTerm.parse(
                source = "[]",
            ).determineType(
                typeScope = TypeScope.Empty,
                valueScope = SyntaxValueScope.Empty,
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = emptyList(),
                ),
                actual = type,
            )
        }

        @Test
        fun testNonEmpty() {
            val type = ExpressionTerm.parse(
                source = "[a, b]",
            ).determineType(
                typeScope = TypeScope.Empty,
                valueScope = FixedStaticValueScope(
                    entries = mapOf(
                        Symbol.of("a") to BoolType,
                        Symbol.of("b") to IntCollectiveType,
                    ),
                ),
            )

            assertEquals(
                expected = OrderedTupleType(
                    elements = listOf(
                        OrderedTupleType.Element(name = null, type = BoolType),
                        OrderedTupleType.Element(name = null, type = IntCollectiveType),
                    ),
                ),
                actual = type,
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testEmpty() {
            val value = ExpressionTerm.parse(
                source = "[]",
            ).evaluate(
                scope = Scope.Empty,
            ).toEvaluatedValue

            assertEquals(
                expected = ArrayTable(
                    elements = emptyList(),
                ),
                actual = value,
            )
        }

        @Test
        fun testNonEmpty() {
            val value = ExpressionTerm.parse(
                source = "[a, b]",
            ).evaluate(
                scope = FixedScope(
                    entries = mapOf(
                        Symbol.of("a") to BoolValue(false),
                        Symbol.of("b") to IntValue(1),
                    ),
                ),
            ).toEvaluatedValue

            assertEquals(
                expected = ArrayTable(
                    elements = listOf(
                        BoolValue(false),
                        IntValue(1),
                    ),
                ),
                actual = value,
            )
        }
    }
}
