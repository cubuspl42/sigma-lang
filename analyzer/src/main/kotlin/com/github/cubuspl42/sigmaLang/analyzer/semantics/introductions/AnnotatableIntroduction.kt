package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface AnnotatableIntroduction : Introduction {
    val annotatedType: MembershipType?
}
