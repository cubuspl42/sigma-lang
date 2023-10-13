package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface Declaration : AnnotatableIntroduction {
    override val annotatedType: MembershipType

    override val computedEffectiveType: Expression.Computation<MembershipType>
        get() = Expression.Computation.pure(annotatedType)
}
