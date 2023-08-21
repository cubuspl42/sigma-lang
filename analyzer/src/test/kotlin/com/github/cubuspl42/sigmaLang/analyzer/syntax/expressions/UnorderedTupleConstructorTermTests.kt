package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class UnorderedTupleConstructorTermTests {
    class ParsingTests {
        @Test
        fun testEmpty() {
            assertEquals(
                expected = UnorderedTupleConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = emptyList()
                ),
                actual = ExpressionSourceTerm.parse("{}"),
            )
        }

        @Test
        fun testSingleEntry() {
            assertEquals(
                expected = UnorderedTupleConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referredName = Symbol.of("baz1"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("{foo: baz1}"),
            )
        }

        @Test
        fun testMultipleEntries() {
            assertEquals(
                expected = UnorderedTupleConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    entries = listOf(
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("foo"),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 6),
                                referredName = Symbol.of("baz1"),
                            ),
                        ),
                        UnorderedTupleConstructorSourceTerm.Entry(
                            name = Symbol.of("bar"),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referredName = Symbol.of("baz2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("{foo: baz1, bar: baz2}"),
            )
        }
    }
}
