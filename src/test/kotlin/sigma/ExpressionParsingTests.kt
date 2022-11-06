package sigma

import sigma.expressions.Call
import sigma.expressions.TableConstructor
import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SourceLocation
import sigma.expressions.SymbolLiteral
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
        fun testReferenceSubject() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol("foo"),
                    ),
                    argument = SymbolLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        symbol = Symbol.of("bar"),
                    ),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = TableConstructor(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        entries = listOf(
                            TableConstructor.SymbolEntryExpression(
                                name = Symbol.of("foo"),
                                value = SymbolLiteral(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 7),
                                    symbol = Symbol.of("bar"),
                                ),
                            ),
                        ),
                    ),
                    argument = SymbolLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 14),
                        symbol = Symbol.of("foo"),
                    ),
                ),
                actual = Expression.parse(
                    source = "{foo = `bar`}[`foo`]",
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
                    argument = TableConstructor(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            TableConstructor.SymbolEntryExpression(
                                name = Symbol.of("bar"),
                                value = SymbolLiteral(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    symbol = Symbol.of("baz"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse(
                    source = "foo{bar = `baz`}",
                ),
            )
        }
    }
}
