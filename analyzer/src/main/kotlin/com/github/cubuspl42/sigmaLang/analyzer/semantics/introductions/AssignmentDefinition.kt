package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap

interface AssignmentDefinition : AnnotatableIntroduction, UserDefinition {
     val assignedBody: Expression
}

val AssignmentDefinition.expressionMap: ExpressionMap
    get() = assignedBody.expressionMap
