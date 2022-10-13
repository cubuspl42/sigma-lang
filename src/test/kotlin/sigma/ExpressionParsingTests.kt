package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = LetExpression(
                    scopeConstructor = ScopeConstructor(
                        binds = mapOf(
                            Symbol("g") to Application(
                                subject = Reference(Symbol("h")),
                                argument = Reference(Symbol("a")),
                            ),
                            Symbol("f") to Reference(Symbol("g")),
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
        fun testIdentifierSubject() {
            assertEquals(
                expected = Application(
                    subject = Reference(Symbol("foo")),
                    argument = Symbol("bar"),
                ),
                actual = Expression.parse("foo[`bar`]"),
            )
        }
    }
}
