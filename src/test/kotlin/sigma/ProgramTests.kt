package sigma

import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertEquals

private const val source1 = """
{'foo': {}}['foo']
"""

class Test1 {
    @Test
    fun test() {
        assertEquals(
            expected = Dict.empty,
            actual = Expression.parse(source1).evaluate(),
        )
    }
}

private const val source2 = """
a@{
    'foo1': c@{},
    'foo2': d@{},
    'bar': a['foo1'][b@{
        'baz1': a['foo1'],
        'baz2': b['baz1'],
    }]
}['bar']
"""

class Test2 {
    @Test
    @Disabled
    fun test() {
        val root = Expression.parse(source2)

        assertEquals(
            expected = Dict.empty,
            actual = root.evaluate(),
        )
    }
}
