package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTest {
    object Form {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = FormExpression(
                    entries = emptyList(),
                ),
                actual = Expression.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = FormExpression(
                    entries = listOf(
                        FormExpression.Entry(
                            key = IdentifierExpression("foo"),
                            value = FormExpression.empty,
                        ),
                    ),
                ),
                actual = Expression.parse("{foo: {}}"),
            )
        }

        @Test
        fun testTwoEntries() {
            assertEquals(
                expected = FormExpression(
                    entries = listOf(
                        FormExpression.Entry(
                            key = IdentifierExpression("foo"),
                            value = FormExpression.empty,
                        ),
                        FormExpression.Entry(
                            key = IdentifierExpression("bar"),
                            value = IdentifierExpression("baz"),
                        ),
                    ),
                ),
                actual = Expression.parse("{foo: {}, bar: baz}"),
            )
        }
    }

    object Identifier {
        @Test
        fun test() {
            assertEquals(
                expected = IdentifierExpression("foo"),
                actual = Expression.parse("foo"),
            )
        }
    }
}
