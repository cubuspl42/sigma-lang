package sigma.syntax

import sigma.evaluation.values.Symbol
import sigma.syntax.typeExpressions.TypeReferenceTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeAliasDefinitionTermTests {
    object ParsingTests {
        @Test
        fun test() {
            val term = StaticStatementTerm.parse(
                source = """
                    typeAlias UserId = Int
                """.trimIndent()
            )

            assertEquals(
                expected = TypeAliasDefinitionTerm(
                    location = SourceLocation(lineIndex = 1, columnIndex = 0),
                    name = Symbol.of("UserId"),
                    definer = TypeReferenceTerm(
                        location = SourceLocation(lineIndex = 1, columnIndex = 19),
                        referee = Symbol.of("Int"),
                    )
                ),
                actual = term,
            )
        }
    }
}
