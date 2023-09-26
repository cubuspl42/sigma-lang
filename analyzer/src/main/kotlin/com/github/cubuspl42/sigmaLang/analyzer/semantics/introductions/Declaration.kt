package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface Declaration : VariableIntroduction, AnnotatableIntroduction {
    override val annotatedTypeThunk: Thunk<MembershipType>

    override val computedEffectiveType: Expression.Computation<MembershipType>
        get() = Expression.Computation.pure(annotatedType)
}

val Declaration.annotatedType: MembershipType
    get() = annotatedTypeThunk.value ?: IllType
