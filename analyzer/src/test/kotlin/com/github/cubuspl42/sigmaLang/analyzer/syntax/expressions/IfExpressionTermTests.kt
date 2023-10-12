package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class IfExpressionTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = ExpressionSourceTerm.parse(
                """
                    %if g (
                        %then t,
                        %else f, 
                    )
                """.trimIndent()
            )
            assertEquals(
                expected = IfExpressionSourceTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    guard = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referredName = Identifier.of("g"),
                    ),
                    trueBranch = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                        referredName = Identifier.of("t"),
                    ),
                    falseBranch = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 10),
                        referredName = Identifier.of("f"),
                    ),
                ),
                actual = term,
            )
        }
    }
}
