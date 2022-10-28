package sigma

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ArgumentTableTests {
    @Test
    fun testRead() {
        val argumentTable = ArgumentTable(
            name = Symbol.of("arg"),
            value = IntValue(1),
        )

        assertEquals(
            expected = IntValue(1),
            actual = argumentTable.read(
                argument = Symbol.of("arg"),
            ),
        )
    }
}
