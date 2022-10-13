package sigma

import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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

    object ScopeTests {
        @Test
        fun test() {
            val scope = assertIs<FunctionValue>(
                value = Expression.parse(
                    source = """
                        {
                            n = `foo`,
                            m = n,
                        }
                    """.trimIndent()
                ).evaluate(),
            )

            assertEquals(
                expected = Symbol("foo"),
                actual = scope.apply(Symbol.of("m")),
            )

            assertEquals(
                expected = Symbol("foo"),
                actual = scope.apply(Symbol.of("n")),
            )
        }
    }


    object Read {
        @Test
        fun testFormSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{foo = `bar`}[`foo`]").evaluate(),
            )
        }

        @Test
        fun testLabelIdentifierSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{foo = `bar`}[`foo`]").evaluate(),
            )
        }

        @Test
        fun testSelfReferring() {
            assertEquals(
                expected = Symbol("baz"),
                actual = Expression.parse("{foo = `baz`, bar = foo}[`bar`]").evaluate(),
            )
        }
    }
}
