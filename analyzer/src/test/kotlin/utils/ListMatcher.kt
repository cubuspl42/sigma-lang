package utils

class ListMatcher<E>(
    private val elements: List<Matcher<E>>,
) : Matcher<List<E>>() {
    override fun match(actual: List<E>) {
        val actualSize = actual.size
        val expectedSize = elements.size

        if (actualSize != expectedSize) {
            throw AssertionError("Unexpected list size. Actual: ${actualSize}, expected: $expectedSize")
        }

        actual.zip(elements).forEachIndexed { index, (element, matcher) ->
            try {
                matcher.match(actual = element)
            } catch (e: AssertionError) {
                throw AssertionError("At index $index: ${e.message}")
            }
        }
    }
}
