package com.github.cubuspl42.sigmaLang.analyzer

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionParsingTests {
    class ReferenceTests {
        @Test
        fun test() {
            assertEquals(
                expected = ReferenceSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    referredName = Symbol("foo"),
                ),
                actual = ExpressionSourceTerm.parse("foo"),
            )
        }
    }

    class CallTests {
        @Test
        fun testDictArgumentShorthand() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Symbol.of("foo")
                    ),
                    argument = UnorderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 3),
                        entries = listOf(
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("bar"),
                                value = IntLiteralSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    value = IntValue(value = 0L),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "foo{bar: 0}",
                ),
            )
        }
    }
}
