package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError

object CollectionMatchers {
    fun <E> eachOnce(
        elements: Set<Matcher<E>>,
    ): Matcher<Collection<E>> = object : Matcher<Collection<E>>() {
        override fun match(actual: Collection<E>) {
            val actualSize = actual.size
            val expectedSize = elements.size

            if (actualSize != expectedSize) {
                throw AssertionError("Unexpected set size. Actual: ${actualSize}, expected: $expectedSize")
            }

            actual.forEach { element ->
                val matchingMatchers = elements.filter {
                    try {
                        it.match(actual = element)
                        true
                    } catch (e: AssertionError) {
                        false
                    }
                }

                if (matchingMatchers.isEmpty()) {
                    throw AssertionError("No matchers matched element '$element'")
                }

                if (matchingMatchers.size > 1) {
                    val matcherDescriptions = matchingMatchers.joinToString { it.description ?: "(null)" }

                    throw AssertionError("More than one matcher matched element '$element': $matcherDescriptions")
                }
            }
        }
    }

    fun <E> eachOnce(
        vararg elements: Matcher<E>,
    ): Matcher<Collection<E>> = eachOnce(elements.toSet())

    fun <E> whereEvery(
        element: Matcher<E>,
    ): Matcher<Collection<E>> = object : Matcher<Collection<E>>() {
        override fun match(actual: Collection<E>) {
            actual.forEach { element.match(actual = it) }
        }
    }

    fun hasSize(
        expectedSize: Int,
    ): Matcher<Collection<Any?>> = object : Matcher<Collection<Any?>>() {
        override fun match(actual: Collection<Any?>) {
            if (actual.size != expectedSize) {
                throw AssertionError("Unexpected collection size. Actual: ${actual.size}, expected: $expectedSize")
            }
        }
    }

    fun isEmpty(): Matcher<Collection<Any?>> = object : Matcher<Collection<Any?>>() {
        override fun match(actual: Collection<Any?>) {
            if (actual.isNotEmpty()) {
                throw AssertionError("Expected empty collection, but got $actual")
            }
        }
    }

    fun isNotEmpty(): Matcher<Set<SemanticError>> = object : Matcher<Set<SemanticError>>() {
        override fun match(actual: Set<SemanticError>) {
            if (actual.isEmpty()) {
                throw AssertionError("Expected non-empty set, but got $actual")
            }
        }
    }
}

fun <E> Matcher<Collection<E>>.whichHasSize(expectedSize: Int): Matcher<Collection<E>> = this.with(
    CollectionMatchers.hasSize(expectedSize = expectedSize)
)
