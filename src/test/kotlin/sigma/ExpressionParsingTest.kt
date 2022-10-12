package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTest {
    object Form {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = Dict(
                    entries = emptyMap(),
                ),
                actual = Expression.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = Dict(
                    entries = mapOf(
                        Symbol("foo") to Dict.empty,
                    ),
                ),
                actual = Expression.parse("{'foo': {}}"),
            )
        }

        @Test
        fun testTwoEntries() {
            assertEquals(
                expected = Dict(
                    entries = mapOf(
                        Symbol("foo") to Dict.empty,
                        Symbol("bar") to Symbol("baz"),
                    ),
                ),
                actual = Expression.parse("{'foo': {}, 'bar': 'baz'}"),
            )
        }

        @Test
        fun testLabeled1() {
            assertEquals(
                expected = Dict(
                    label = "l1",
                    entries = emptyMap(),
                ),
                actual = Expression.parse("l1@{}"),
            )
        }

        @Test
        fun testLabeled2() {
            assertEquals(
                expected = Dict(
                    label = "l1",
                    entries = mapOf(
                        Symbol("foo") to Dict.empty,
                        Symbol("bar") to Symbol("baz"),
                    ),
                ),
                actual = Expression.parse("l1@{'foo': {}, 'bar': 'baz'}"),
            )
        }
    }

    object Identifier {
        @Test
        fun test() {
            assertEquals(
                expected = Reference("foo"),
                actual = Expression.parse("foo"),
            )
        }
    }

    object AbstractionTests {
        @Test
        fun test() {
            assertEquals(
                expected = Abstraction(
                    argument = "x",
                    image = Dict(
                        entries = mapOf(
                            Symbol("foo") to Dict.empty,
                        ),
                    ),
                ),
                actual = Expression.parse("x => {'foo': {}}"),
            )
        }
    }

    object Read {
        @Test
        fun testIdentifierSubject() {
            assertEquals(
                expected = Application(
                    subject = Reference("foo"),
                    key = Symbol("bar"),
                ),
                actual = Expression.parse("foo['bar']"),
            )
        }

        @Test
        fun testFormSubject() {
            assertEquals(
                expected = Application(
                    subject = Dict(
                        entries = mapOf(
                            Symbol("foo") to Dict.empty,
                        ),
                    ),
                    key = Symbol("foo"),
                ),
                actual = Expression.parse("{'foo': {}}['foo']"),
            )
        }
    }
}
