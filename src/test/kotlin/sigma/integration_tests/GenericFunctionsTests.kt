package sigma.integration_tests

import sigma.SyntaxTypeScope
import sigma.SyntaxValueScope
import sigma.syntax.expressions.ExpressionTerm
import sigma.semantics.types.IntLiteralType
import sigma.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GenericFunctionsTests {
    @Test
    fun test() {
        val result = ExpressionTerm.parse(
            """
                let {
                    id = ![t] [t: t] => t,
                } in id[1]
            """.trimIndent(),
        )

        assertEquals(
            expected = IntLiteralType(
                value = IntValue(value = 1),
            ),
            actual = result.determineType(
                typeScope = SyntaxTypeScope.Empty,
                valueScope = SyntaxValueScope.Empty,
            ),
        )
    }
}
