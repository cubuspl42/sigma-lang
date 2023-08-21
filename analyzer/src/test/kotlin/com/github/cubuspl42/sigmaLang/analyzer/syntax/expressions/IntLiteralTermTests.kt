package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

class IntLiteralTermTests {
    class ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = IntLiteralSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    IntValue(123),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "123",
                ),
            )
        }
    }
}
