package com.github.cubuspl42.sigmaLang.analyzer

import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.CallSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.SymbolLiteralSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
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
        fun testDictSubject() {
            assertEquals(
                expected = CallSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = UnorderedTupleConstructorSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        entries = listOf(
                            UnorderedTupleConstructorSourceTerm.Entry(
                                name = Symbol.of("foo"),
                                value = SymbolLiteralSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                    symbol = Symbol.of("bar"),
                                ),
                            ),
                        ),
                    ),
                    argument = SymbolLiteralSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 13),
                        symbol = Symbol.of("foo"),
                    ),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "{foo: `bar`}(`foo`)",
                ),
            )
        }

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
                                value = SymbolLiteralSourceTerm(
                                    location = SourceLocation(lineIndex = 1, columnIndex = 9),
                                    symbol = Symbol.of("baz"),
                                ),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "foo{bar: `baz`}",
                ),
            )
        }
    }
}