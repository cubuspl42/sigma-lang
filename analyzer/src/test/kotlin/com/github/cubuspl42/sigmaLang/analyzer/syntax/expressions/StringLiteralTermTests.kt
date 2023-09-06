package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import kotlin.test.Test
import kotlin.test.assertEquals

class StringLiteralTermTests {
    class ParsingTests {
        @Test
        fun test() {
            assertEquals(
                expected = StringLiteralSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    value = StringValue(value = "abcd"),
                ),
                actual = ExpressionSourceTerm.parse(
                    source = "\"abcd\"",
                ),
            )
        }
    }
}
