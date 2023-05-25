package sigma.syntax.expressions

import sigma.Thunk
import sigma.syntax.SourceLocation
import sigma.evaluation.values.ComputableFunctionValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.FixedScope
import kotlin.test.Test
import kotlin.test.assertEquals

class CallTests {
    object ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referee = Symbol.of("bar"),
                    ),
                ),
                actual = ExpressionTerm.parse("foo(bar)"),
            )
        }

        @Test
        fun testFieldReadSubject() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = FieldReadTerm(

                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        subject = ReferenceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 0),
                            referee = Symbol.of("foo"),
                        ),
                        fieldName = Symbol.of("bar"),
                    ),
                    argument = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        referee = Symbol.of("baz"),
                    ),
                ),
                actual = ExpressionTerm.parse("foo.bar(baz)"),
            )
        }

        @Test
        fun testUnorderedTupleArgumentSugar() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = UnorderedTupleLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleLiteralTerm.Entry(
                                name = Symbol.of("arg1"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("value1"),
                                ),
                            ),
                            UnorderedTupleLiteralTerm.Entry(
                                name = Symbol.of("arg2"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 24),
                                    referee = Symbol.of("value2"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("foo{arg1: value1, arg2: value2}"),
            )
        }

        @Test
        fun testOrderedTupleArgumentSugar() {
            assertEquals(
                expected = CallTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = OrderedTupleLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        elements = listOf(
                            ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("value1"),
                            ),
                            ReferenceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionTerm.parse("foo[value1, value2]"),
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
                actual = ExpressionTerm.parse("sq(3)").evaluate(
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
                actual = ExpressionTerm.parse("dict(2)").evaluate(
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
