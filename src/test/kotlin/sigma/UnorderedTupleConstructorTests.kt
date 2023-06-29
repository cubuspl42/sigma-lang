package sigma

import org.junit.jupiter.api.assertThrows
import sigma.syntax.expressions.UnorderedTupleConstructorTerm
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.UnorderedTupleConstructorTerm.DuplicatedNameError
import sigma.semantics.types.BoolType
import sigma.semantics.types.IntCollectiveType
import sigma.semantics.types.UnorderedTupleType
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.evaluation.values.FixedStaticValueScope
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

object UnorderedTupleConstructorTests {
    object ParsingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList()
                ),
                actual = ExpressionTerm.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = UnorderedTupleConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
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
                expected = UnorderedTupleConstructorTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referee = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleConstructorTerm.Entry(
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
