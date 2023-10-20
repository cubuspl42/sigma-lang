package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike

// Thought: Make a data class?
interface Definition : Introduction {
    val bodyStub: Expression.Stub<Expression>

    val body: Expression
        get() = bodyStub.resolved

    val valueThunk: Thunk<Value>
        get() = (body.classified as ConstExpression).valueThunk

    val errors: Set<SemanticError>
        get() = body.errors

    val computedBodyType: Expression.Computation<TypeAlike>
        get() = bodyStub.resolved.inferredTypeOrIllType
}

fun Definition(
    name: Symbol,
    bodyStub: Expression.Stub<Expression>,
): Definition = object : Definition {
    override val name = name

    override val bodyStub = bodyStub
}

fun Definition(
    name: Symbol,
    body: Expression,
): Definition = object : Definition {
    override val name = name

    override val bodyStub = Expression.Stub.of(body)
}
