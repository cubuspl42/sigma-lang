package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier

abstract class ShadowExpression {
    abstract val rawExpression: Expression
}

fun ShadowExpression.call(
    passedArgument: ShadowExpression,
): Expression = rawExpression.call(
    passedArgument = passedArgument.rawExpression,
)

fun ShadowExpression.readField(
    fieldName: Identifier,
): ShadowExpression = rawExpression.readField(
    fieldName = fieldName,
)
