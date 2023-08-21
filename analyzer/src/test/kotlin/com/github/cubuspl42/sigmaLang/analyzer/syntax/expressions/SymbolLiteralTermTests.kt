package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class SymbolLiteralTermTests {
    class ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = SymbolLiteralSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    symbol = Symbol.of("foo"),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "`foo`",
                ),
            )
        }
    }
}
