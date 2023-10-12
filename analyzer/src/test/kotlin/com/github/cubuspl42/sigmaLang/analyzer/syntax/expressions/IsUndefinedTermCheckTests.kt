package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import kotlin.test.Test
import kotlin.test.assertEquals

class IsUndefinedTermCheckTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = ExpressionSourceTerm.parse(
                source = "%isUndefined foo",
            )

            assertEquals(
                expected = IsUndefinedCheckSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    argument = ReferenceSourceTerm(
                        SourceLocation(lineIndex = 1, columnIndex = 13),
                        referredName = Identifier.of("foo"),
                    ),
                ),
                actual = term,
            )
        }
    }
}
