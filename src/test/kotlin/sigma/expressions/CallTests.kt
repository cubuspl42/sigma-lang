package sigma.expressions

import org.junit.jupiter.api.assertThrows
import sigma.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class CallTests {
    object EvaluationTests {
        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{foo: `bar`}[`foo`]").evaluateAsRoot(),
            )
        }

        @Test
        fun testSelfReferring() {
            assertThrows<RuntimeException> {
                Expression.parse("{foo: `baz`, bar: foo}[`bar`]").evaluateAsRoot()
            }
        }
    }
}
