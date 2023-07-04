package sigma.syntax.expressions

import sigma.evaluation.Thunk
import sigma.syntax.SourceLocation
import sigma.evaluation.values.ComputableFunctionValue
import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.FixedScope
import kotlin.test.Test
import kotlin.test.assertEquals

class CallTermTests {
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
                    argument = UnorderedTupleConstructorTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleConstructorTerm.Entry(
                                name = Symbol.of("arg1"),
                                value = ReferenceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("value1"),
                                ),
                            ),
                            UnorderedTupleConstructorTerm.Entry(
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
                    argument = OrderedTupleConstructorTerm(
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
}
