package utils

import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.MembershipType
import kotlin.test.assertTrue

fun assertTypeIsEquivalent(
    expected: MembershipType,
    actual: MembershipType,
) {
    assertTrue(
        actual = expected.isEquivalentTo(otherType = actual),
        message = "Expected ${expected.dump()} to be equivalent to ${actual.dump()}",
    )
}

fun assertTypeIsNonEquivalent(
    expected: MembershipType,
    actual: MembershipType,
) {
    assertTrue(
        actual = expected.isNonEquivalentTo(otherType = actual),
        message = "Expected ${expected.dump()} to be equivalent to ${actual.dump()}",
    )
}

fun <T> assertMatchesEachOnce(
    actual: Collection<T>,
    /**
     * Assertion blocks keyed by labels
     */
    blocks: Map<String, (T) -> Unit>,
) {
    val actualSize = actual.size
    val expectedSize = blocks.size

    if (actualSize != expectedSize) {
        throw AssertionError("Unexpected collection size. Actual: ${actualSize}, expected: $expectedSize")
    }

    blocks.forEach { (name, block) ->
        val matchingElements = actual.filter {
            try {
                block(it)
                true
            } catch (e: AssertionError) {
                false
            }
        }

        if (matchingElements.isEmpty()) {
            throw AssertionError("No elements matched block '$name'")
        }

        if (matchingElements.size > 1) {
            throw AssertionError("More than one element matched block '$name': $matchingElements")
        }
    }
}

fun <T> assertMatchesEachInOrder(
    actual: Collection<T>,
    /**
     * Assertion blocks keyed by labels
     */
    blocks: List<(T) -> Unit>,
) {
    val actualSize = actual.size
    val expectedSize = blocks.size

    if (actualSize != expectedSize) {
        throw AssertionError("Unexpected collection size. Actual: ${actualSize}, expected: $expectedSize")
    }

    actual.zip(blocks).forEachIndexed { index, (element, block) ->
        try {
            block(element)
        } catch (e: AssertionError) {
            throw AssertionError("At index $index: ${e.message}")
        }
    }
}
