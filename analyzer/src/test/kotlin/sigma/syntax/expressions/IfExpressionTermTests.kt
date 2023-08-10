package sigma.syntax.expressions

import sigma.evaluation.values.Symbol
import sigma.syntax.SourceLocation
import kotlin.test.Test
import kotlin.test.assertEquals

class IfExpressionTermTests {
    class ParsingTests {
        @Test
        fun testSimple() {
            val term = ExpressionTerm.parse(
                """
                    %if g (
                        %then t,
                        %else f, 
                    )
                """.trimIndent()
            )
            assertEquals(
                expected = IfExpressionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    guard = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 4),
                        referee = Symbol.of("g"),
                    ),
                    trueBranch = ReferenceTerm(
                        location = SourceLocation(lineIndex = 2, columnIndex = 10),
                        referee = Symbol.of("t"),
                    ),
                    falseBranch = ReferenceTerm(
                        location = SourceLocation(lineIndex = 3, columnIndex = 10),
                        referee = Symbol.of("f"),
                    ),
                ),
                actual = term,
            )
        }
    }
}
