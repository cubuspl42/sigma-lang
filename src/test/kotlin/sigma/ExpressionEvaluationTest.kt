package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTest {
    object Form {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = ObjectValue(entries = emptyMap()),
                actual = Expression.parse("{}").evaluate(),
            )
        }

        @Test
        fun testTwoEntries() {
            assertEquals(
                expected = ObjectValue(
                    entries = mapOf(
                        IdentifierValue("foo") to ObjectValue.empty,
                        IdentifierValue("bar") to ObjectValue.empty,
                    ),
                ),
                actual = Expression.parse("{foo: {}, bar: {}}").evaluate(),
            )
        }
    }

    object Identifier {
        @Test
        fun test() {
            assertEquals(
                expected = IdentifierValue("foo"),
                actual = Expression.parse("foo").evaluate(),
            )
        }
    }
}
