package sigma

import sigma.expressions.Application
import sigma.expressions.DictConstructor
import sigma.expressions.Expression
import sigma.expressions.IntLiteral
import sigma.expressions.Reference
import sigma.expressions.SymbolLiteral
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object DictConstructorTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictConstructor(
                    content = listOf(
                        DictConstructor.SymbolAssignment(
                            name = Symbol.of("foo"),
                            value = Reference(Symbol.of("baz1")),
                        ),
                        DictConstructor.SymbolAssignment(
                            name = Symbol.of("bar"),
                            value = Reference(Symbol.of("baz2")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, bar = baz2}"),
            )
        }

        @Test
        fun testWithArbitrary() {
            assertEquals(
                expected = DictConstructor(
                    content = listOf(
                        DictConstructor.SymbolAssignment(
                            name = Symbol.of("foo"),
                            value = Reference(Symbol.of("baz1")),
                        ),
                        DictConstructor.ArbitraryAssignment(
                            key = Reference(Symbol.of("baz")),
                            value = Reference(Symbol.of("baz2")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo = baz1, [baz] = baz2}"),
            )
        }

        @Test
        fun testArray() {
            assertEquals(
                expected = DictConstructor(
                    content = listOf(
                        DictConstructor.ArbitraryAssignment(
                            key = IntLiteral.of(0),
                            value = Reference(Symbol.of("foo")),
                        ),
                        DictConstructor.ArbitraryAssignment(
                            key = IntLiteral.of(1),
                            value = Reference(Symbol.of("bar")),
                        ),
                        DictConstructor.ArbitraryAssignment(
                            key = IntLiteral.of(2),
                            value = Reference(Symbol.of("baz")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo, bar, baz}"),
            )
        }
    }

    object ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = Reference(Symbol("foo")),
                actual = Expression.parse("foo"),
            )
        }
    }

    object ApplicationTests {
        @Test
        fun testReferenceSubject() {
            assertEquals(
                expected = Application(
                    subject = Reference(Symbol("foo")),
                    argument = SymbolLiteral.of("bar"),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Application(
                    subject = DictConstructor(
                        content = listOf(
                            DictConstructor.SymbolAssignment(
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
                expected = Application(
                    subject = Reference(Symbol.of("foo")),
                    argument = DictConstructor(
                        content = listOf(
                            DictConstructor.SymbolAssignment(
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
