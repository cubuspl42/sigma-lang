package sigma.syntax.expressions

import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class CallTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referee = Symbol.of("bar"),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo(bar)"),
            )
        }

        @Test
        fun testFieldReadSubject() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = FieldReadSourceTerm(

                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        subject = ReferenceSourceTerm(
                            location = SourceLocation(lineIndex = 1, columnIndex = 0),
                            referee = Symbol.of("foo"),
                        ),
                        fieldName = Symbol.of("bar"),
                    ),
                    argument = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 8),
                        referee = Symbol.of("baz"),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo.bar(baz)"),
            )
        }

        @Test
        fun testUnorderedTupleArgumentSugar() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = UnorderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("arg1"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 10),
                                    referee = Symbol.of("value1"),
                                ),
                            ),
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("arg2"),
                                value = ReferenceSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 24),
                                    referee = Symbol.of("value2"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo{arg1: value1, arg2: value2}"),
            )
        }

        @Test
        fun testOrderedTupleArgumentSugar() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referee = Symbol.of("foo"),
                    ),
                    argument = OrderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        elements = listOf(
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 4),
                                referee = Symbol.of("value1"),
                            ),
                            ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 12),
                                referee = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("foo[value1, value2]"),
            )
        }
    }
}
