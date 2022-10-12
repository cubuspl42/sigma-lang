package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTest {
    object DictTest {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = Dict(entries = emptyMap()),
                actual = Expression.parse("{}").evaluate(),
            )
        }

        @Test
        fun testTwoEntries() {
            assertEquals(
                expected = Dict(
                    entries = mapOf(
                        Symbol("foo") to Dict.empty,
                        Symbol("bar") to Dict.empty,
                    ),
                ),
                actual = Expression.parse(
                    source = "{'foo': {}, 'bar': {}}",
                ).evaluate(),
            )
        }

        @Test
        fun testLabeled() {
            assertEquals(
                expected = Dict(
                    label = "a",
                    entries = mapOf(
                        Symbol("foo") to Dict.empty,
                        Symbol("bar") to Application(
                            subject = Reference("a"),
                            key = Symbol("foo"),
                        ),
                    ),
                ),
                actual = Expression.parse(
                    source = "a@{'foo': {}, 'bar': a['foo']}",
                ).evaluate(),
            )
        }
    }

    object Read {
        @Test
        fun testFormSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{'foo': 'bar'}['foo']").evaluate(),
            )
        }

        @Test
        fun testLabelIdentifierSubject() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse("{'foo': 'bar'}['foo']").evaluate(),
            )
        }

        @Test
        fun testSelfReferring() {
            assertEquals(
                expected = Symbol("baz"),
                actual = Expression.parse("a@{'foo': 'baz', 'bar': a['foo']}['bar']").evaluate(),
            )
        }
    }
}
