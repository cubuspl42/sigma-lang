package sigma.integration_tests

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.syntax.expressions.Expression
import sigma.semantics.types.IntLiteralType
import sigma.values.IntValue
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GenericFunctionsTests {
    @Test
    fun test() {
        val result = Expression.parse(
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
                typeScope = StaticTypeScope.Empty,
                valueScope = StaticValueScope.Empty,
            ),
        )
    }
}
