package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldReadTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            assertEquals(
                expected = FieldReadSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    subject = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 0),
                        referredName = Symbol.of("foo"),
                    ),
                    fieldName = Symbol.of("bar"),
                ),
                actual = ExpressionSourceTerm.parse("foo.bar"),
            )
        }
    }
}
