package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap

interface EmbodiedUserDefinition : AnnotatableIntroduction, UserDefinition {
    val body: Expression
}

val EmbodiedUserDefinition.expressionMap: ExpressionMap
    get() = body.expressionMap
