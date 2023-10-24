package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike

// Thought: Nuke? Same as expression now?
interface Definition : Introduction {
    val bodyStub: Stub<Expression>

    val body: Expression
        get() = bodyStub.resolved

    val valueThunk: Thunk<Value>
        get() = (body.classified as ConstExpression).valueThunk

    val errors: Set<SemanticError>
        get() = body.errors

    val computedBodyType: Expression.Computation<TypeAlike>
        get() = (body as Expression).inferredTypeOrIllType
}

fun Definition(
    bodyStub: Stub<Expression>,
): Definition = object : Definition {
    override val bodyStub = bodyStub
}

fun Definition(
    body: Expression,
): Definition = object : Definition {
    override val bodyStub = Stub.of(body)
}
