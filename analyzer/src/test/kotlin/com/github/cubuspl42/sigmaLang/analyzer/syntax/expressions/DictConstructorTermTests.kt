package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class DictConstructorTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = DictConstructorSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    associations = listOf(
                        DictConstructorSourceTerm.Association(
                            key = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 2),
                                referredName = Symbol.of("foo"),
                            ),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 8),
                                referredName = Symbol.of("value1"),
                            ),
                        ),
                        DictConstructorSourceTerm.Association(
                            key = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 17),
                                referredName = Symbol.of("baz"),
                            ),
                            value = ReferenceSourceTerm(
                                location = SourceLocation(lineIndex = 1, columnIndex = 23),
                                referredName = Symbol.of("value2"),
                            ),
                        ),
                    ),
                ),
                actual = ExpressionSourceTerm.parse("{[foo]: value1, [baz]: value2}"),
            )
        }
    }
}
