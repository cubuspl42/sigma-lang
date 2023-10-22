package utils

object ListMatchers {
    fun <E> inOrder(
        vararg elements: Matcher<E>,
    ): Matcher<List<E>> = object : Matcher<List<E>>() {
        override fun match(actual: List<E>) {
            val actualSize = actual.size
            val expectedSize = elements.size

            if (actualSize != expectedSize) {
                throw AssertionError("Unexpected list size. Actual: ${actualSize}, expected: $expectedSize")
            }

            elements.forEachIndexed { index, element ->
                element.match(actual = actual[index])
            }
        }
    }
}
