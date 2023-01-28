package sigma

import sigma.syntax.expressions.Call
import sigma.syntax.expressions.UnorderedTupleLiteral
import sigma.syntax.expressions.Expression
import sigma.syntax.expressions.Reference
import sigma.syntax.SourceLocation
import sigma.syntax.expressions.SymbolLiteral
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = Reference(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    referee = Symbol("foo"),
                ),
                actual = Expression.parse("foo"),
            )
        }
    }

    object CallTests {
        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = UnorderedTupleLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        entries = listOf(
                            UnorderedTupleLiteral.Entry(
                                name = Symbol.of("foo"),
                                value = SymbolLiteral(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    symbol = Symbol.of("bar"),
                                ),
                            ),
                        ),
                    ),
                    argument = SymbolLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 13),
                        symbol = Symbol.of("foo"),
                    ),
                ),
                actual = Expression.parse(
                    source = "{foo: `bar`}(`foo`)",
                ),
            )
        }

        @Test
        fun testDictArgumentShorthand() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo")
                    ),
                    argument = UnorderedTupleLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleLiteral.Entry(
                                name = Symbol.of("bar"),
                                value = SymbolLiteral(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    symbol = Symbol.of("baz"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse(
                    source = "foo{bar: `baz`}",
                ),
            )
        }
    }
}
