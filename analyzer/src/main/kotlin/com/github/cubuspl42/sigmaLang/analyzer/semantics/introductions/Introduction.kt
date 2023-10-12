package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

sealed interface Introduction {
    val name: Identifier

    val computedEffectiveType: Expression.Computation<MembershipType>
}
