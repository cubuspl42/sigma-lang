package sigma

import sigma.expressions.Expression
import sigma.expressions.Reference
import sigma.expressions.SymbolLiteral
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TableConstructorTests {
    @Test
    fun testConstruct() {
        val tableConstructor = mapOf(
            SymbolLiteral.of("foo") to Reference(referee = Symbol.of("a")),
            SymbolLiteral.of("bar") to Reference(referee = Symbol.of("b")),
        )

        val expressionTable = tableConstructor.mapKeys {
            it.key.evaluate(context = EmptyTable)
        }

        assertEquals(
            expected = mapOf<Value, Expression>(
                Symbol.of("foo") to Reference(referee = Symbol.of("a")),
                Symbol.of("bar") to Reference(referee = Symbol.of("b")),
            ),
            actual = expressionTable,
        )
    }
}
