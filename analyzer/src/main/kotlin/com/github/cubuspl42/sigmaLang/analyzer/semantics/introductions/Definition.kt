package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.QuasiExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface Definition : Introduction {
    val body: QuasiExpression

    override val computedEffectiveType: Expression.Computation<MembershipType>
        get() = body.inferredTypeOrIllType
}
