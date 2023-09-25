package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

sealed interface Introduction {
    val name: Symbol

    val effectiveTypeThunk: Thunk<MembershipType>
}
