package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
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
    }
}
