package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TableConstructorTests {
    @Test
    fun testConstruct() {
        val tableConstructor = TableConstructor(
            entries = mapOf(
                SymbolLiteral.of("foo") to Reference(referee = Symbol.of("a")),
                SymbolLiteral.of("bar") to Reference(referee = Symbol.of("b")),
            ),
        )

        val expressionTable = tableConstructor.construct(
            environment = EmptyTable,
        )

        assertEquals(
            expected = ExpressionTable(
                entries = mapOf(
                    Symbol.of("foo") to Reference(referee = Symbol.of("a")),
                    Symbol.of("bar") to Reference(referee = Symbol.of("b")),
                )
            ),
            actual = expressionTable,
        )
    }
}
