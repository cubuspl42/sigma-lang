package sigma

import org.junit.jupiter.api.assertThrows
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.UnorderedTupleLiteralTerm.DuplicatedNameError
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleLiteralTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList()
                ),
                actual = ExpressionTerm.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{foo: baz1}"),
            )
        }

        @Test
        fun testMultipleEntries() {
            assertEquals(
                expected = UnorderedTupleLiteralTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleLiteralTerm.Entry(
                            name = Symbol.of("bar"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referee = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("{foo: baz1, bar: baz2}"),
            )
        }
    }
}
