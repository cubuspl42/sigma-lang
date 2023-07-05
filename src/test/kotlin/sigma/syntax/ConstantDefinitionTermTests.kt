package sigma.syntax

import sigma.evaluation.values.IntValue
import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.IntLiteralTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantDefinitionTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = StaticStatementTerm.parse(
                source = """
                    const name1 = 123
                """.trimIndent()
            )

            assertEquals(
                expected = ConstantDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("name1"),
                    type = null,
                    definer = IntLiteralTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 14),
                        value = IntValue(value = 123L),
                    ),
                ),
                actual = term,
            )
        }
    }
}
