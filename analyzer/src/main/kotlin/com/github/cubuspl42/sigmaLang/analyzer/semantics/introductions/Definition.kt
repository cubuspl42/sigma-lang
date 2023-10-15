package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IllType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType

interface Definition : Introduction {
    val bodyStub: Expression.Stub<Expression>

    val body: Expression
        get() = bodyStub.resolved

    val valueThunk: Thunk<Value>
        get() = (body.classified as ConstExpression).valueThunk

    val errors: Set<SemanticError>
        get() = body.errors

    val computedBodyType: Expression.Computation<MembershipType>
        get() = bodyStub.resolved.inferredTypeOrIllType
}
