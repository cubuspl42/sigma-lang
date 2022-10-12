package sigma

import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTest {
    object TableTest {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = Table.empty,
                actual = Expression.parse("{}").evaluate(),
            )
        }

        @Test
        fun testTwoEntries() {
            assertEquals(
                expected = Table(
                    scope = Scope.Empty,
                    entries = mapOf(
                        Symbol("foo") to TableExpression.empty,
                        Symbol("bar") to TableExpression.empty,
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
                expected = Table(
                    scope = Scope.Empty,
                    label = "a",
                    entries = mapOf(
                        Symbol("foo") to TableExpression.empty,
                        Symbol("bar") to Application(
                            subject = Reference("a"),
                            argument = Symbol("foo"),
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

    object AbstractionTests {
        @Test
        @Disabled
        fun test() {
            assertEquals(
                expected = Symbol("bar"),
                actual = Expression.parse(
                    source = "x => {'foo': x}",
                ).evaluate(),
            )
        }
    }
}
