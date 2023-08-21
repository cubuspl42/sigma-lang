package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
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
                        referredName = Symbol.of("g"),
                    ),
                    trueBranch = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                        referredName = Symbol.of("t"),
                    ),
                    falseBranch = ReferenceSourceTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 10),
                        referredName = Symbol.of("f"),
                    ),
                ),
                actual = term,
            )
        }
    }
}
