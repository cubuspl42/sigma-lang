package sigma

import sigma.expressions.Call
import sigma.expressions.TableConstructor
import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SymbolLiteral
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = Reference(Symbol("foo")),
                actual = Expression.parse("foo"),
            )
        }
    }

    object CallTests {
        @Test
        fun testReferenceSubject() {
            assertEquals(
                expected = Call(
                    subject = Reference(Symbol("foo")),
                    argument = SymbolLiteral.of("bar"),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Call(
                    subject = TableConstructor(
                        entries = listOf(
                            TableConstructor.SymbolEntryExpression(
                                name = Symbol.of("foo"),
                                value = SymbolLiteral.of("bar"),
                            ),
                        ),
                    ),
                    argument = SymbolLiteral.of("foo"),
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
                    subject = Reference(Symbol.of("foo")),
                    argument = TableConstructor(
                        entries = listOf(
                            TableConstructor.SymbolEntryExpression(
                                name = Symbol.of("bar"),
                                value = SymbolLiteral.of("baz"),
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
