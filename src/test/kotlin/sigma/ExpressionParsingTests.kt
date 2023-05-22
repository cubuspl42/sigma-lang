package sigma

import sigma.syntax.expressions.CallTerm
import sigma.syntax.expressions.UnorderedTupleLiteralTerm
import sigma.syntax.expressions.ReferenceTerm
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.ExpressionTerm
import sigma.syntax.expressions.SymbolLiteralTerm
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = ReferenceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    referee = Symbol("foo"),
                ),
                actual = ExpressionTerm.parse("foo"),
            )
        }
    }

    object CallTests {
        @Test
        fun testDictSubject() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = UnorderedTupleLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        entries = listOf(
                            UnorderedTupleLiteralTerm.Entry(
                                name = Symbol.of("foo"),
                                value = SymbolLiteralTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    symbol = Symbol.of("bar"),
                                ),
                            ),
                        ),
                    ),
                    argument = SymbolLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 13),
                        symbol = Symbol.of("foo"),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "{foo: `bar`}(`foo`)",
                ),
            )
        }

        @Test
        fun testDictArgumentShorthand() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo")
                    ),
                    argument = UnorderedTupleLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleLiteralTerm.Entry(
                                name = Symbol.of("bar"),
                                value = SymbolLiteralTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    symbol = Symbol.of("baz"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse(
                    source = "foo{bar: `baz`}",
                ),
            )
        }
    }
}
