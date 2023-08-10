package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.expressions.ReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantDefinitionTermTests {
    class ParsingTests {
        @Test
        fun test() {
            val term = NamespaceEntryTerm.parse(
                source = """
                    %const UserId = Int
                """.trimIndent()
            )

            assertEquals(
                expected = ConstantDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("UserId"),
                    body = ReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 16),
                        referee = Symbol.of("Int"),
                    )
                ),
                actual = term,
            )
        }
    }
}
