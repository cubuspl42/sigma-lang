package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantDefinitionTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = StaticStatementTerm.parse(
                source = """
                    const UserId = Int
                """.trimIndent()
            )

            assertEquals(
                expected = ConstantDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("UserId"),
                    body = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 19),
                        referee = Symbol.of("Int"),
                    )
                ),
                actual = term,
            )
        }
    }
}
