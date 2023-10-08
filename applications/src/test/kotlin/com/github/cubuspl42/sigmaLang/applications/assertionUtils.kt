package com.github.cubuspl42.sigmaLang.applications

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
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
