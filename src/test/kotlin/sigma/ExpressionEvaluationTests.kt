package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTests {
    object LetExpressionTests {
        @Test
        fun test() {
            assertEquals(
                expected = Symbol("foo"),
                actual = Expression.parse(
                    source = """
                        let {
                            n = `foo`,
                            m = n,
                        } in m
                    """.trimIndent()
                ).evaluate(),
            )
        }
    }

    object ApplicationTests {
        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{foo = `bar`}[`foo`]").evaluate(),
            )
        }

        @Test
        fun testSelfReferring() {
            assertEquals(
                expected = UndefinedValue.undefined,
                actual = Expression.parse("{foo = `baz`, bar = foo}[`bar`]").evaluate(),
            )
        }
    }
}
