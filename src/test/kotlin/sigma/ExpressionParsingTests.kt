package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object DictConstructorTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictConstructor(
                    content = TableConstructor(
                        entries = mapOf(
                            Symbol.of("foo") to Reference(Symbol.of("baz1")),
                            Symbol.of("bar") to Reference(Symbol.of("baz2")),
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
                    content = TableConstructor(
                        entries = mapOf(
                            Symbol.of("foo") to Reference(Symbol.of("baz1")),
                            Reference(Symbol.of("baz")) to Reference(Symbol.of("baz2"))
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
                    content = TableConstructor(
                        entries = mapOf(
                            IntValue(0) to Reference(Symbol.of("foo")),
                            IntValue(1) to Reference(Symbol.of("bar")),
                            IntValue(2) to Reference(Symbol.of("baz")),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo, bar, baz}"),
            )
        }
    }

    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = LetExpression(
                    scope = TableConstructor(
                        mapOf(
                            Symbol.of("g") to Application(
                                subject = Reference(Symbol("h")),
                                argument = Reference(Symbol("a")),
                            ),
                            Symbol.of("f") to Reference(Symbol("g")),
                        ),
                    ),
                    result = Application(
                        subject = Reference(Symbol("f")),
                        argument = Reference(Symbol("x")),
                    ),
                ),
                actual = Expression.parse(
                    source = """
                        let {
                            g = h[a],
                            f = g,
                        } in f[x]
                    """.trimIndent()
                ),
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
                    argument = Symbol("bar"),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Application(
                    subject = DictConstructor(
                        content = TableConstructor(
                            entries = mapOf(
                                Symbol.of("foo") to Symbol.of("bar"),
                            ),
                        ),
                    ),
                    argument = Symbol("foo"),
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
                    argument = DictConstructor.of(
                        entries = mapOf(
                            Symbol.of("bar") to Symbol.of("baz"),
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
