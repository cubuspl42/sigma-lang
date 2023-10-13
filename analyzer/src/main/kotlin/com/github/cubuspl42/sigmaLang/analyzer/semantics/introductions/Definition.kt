package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface Definition : Introduction {
    val bodyStub: Expression.Stub<Expression>

    val errors: Set<SemanticError>

    override val computedEffectiveType: Expression.Computation<MembershipType>
        get() = bodyStub.resolved.computedAnalysis.transform { it?.inferredType ?: IllType }
}
