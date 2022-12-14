package sigma.expressions

import org.junit.jupiter.api.assertThrows
import sigma.Thunk
import sigma.values.ComputableFunctionValue
import sigma.values.IntValue
import sigma.values.Symbol
import sigma.values.Value
import sigma.values.tables.DictTable
import sigma.values.tables.FixedScope
import kotlin.test.Test
import kotlin.test.assertEquals

class CallTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referee = Symbol.of("bar"),
                    ),
                ),
                actual = Expression.parse("foo(bar)"),
            )
        }

        @Test
        fun testUnorderedTupleArgumentSugar() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = UnorderedTupleLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleLiteral.Entry(
                                name = Symbol.of("arg1"),
                                value = Reference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("value1"),
                                ),
                            ),
                            UnorderedTupleLiteral.Entry(
                                name = Symbol.of("arg2"),
                                value = Reference(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 24),
                                    referee = Symbol.of("value2"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("foo{arg1: value1, arg2: value2}"),
            )
        }

        @Test
        fun testOrderedTupleArgumentSugar() {
            assertEquals(
                expected = Call(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = Reference(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = OrderedTupleLiteral(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        elements = listOf(
                            Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("value1"),
                            ),
                            Reference(
                                location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = Expression.parse("foo[value1, value2]"),
            )
        }
    }

    object EvaluationTests {
        @Test
        fun testSimple() {
            val sq = object : ComputableFunctionValue() {
                override fun apply(argument: Value): Thunk {
                    val n = argument as IntValue
                    return IntValue(n.value * n.value)
                }
            }

            assertEquals(
                expected = IntValue(9),
                actual = Expression.parse("sq(3)").evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("sq") to sq,
                        )
                    ),
                ),
            )
        }

        @Test
        fun testDictSubject() {
            assertEquals(
                expected = Symbol.of("two"),
                actual = Expression.parse("dict(2)").evaluate(
                    scope = FixedScope(
                        entries = mapOf(
                            Symbol.of("dict") to DictTable(
                                entries = mapOf(
                                    IntValue(1) to Symbol.of("one"),
                                    IntValue(2) to Symbol.of("two"),
                                    IntValue(3) to Symbol.of("three"),
                                ),
                            ),
                        ),
                    ),
                ),
            )
        }
    }
}
