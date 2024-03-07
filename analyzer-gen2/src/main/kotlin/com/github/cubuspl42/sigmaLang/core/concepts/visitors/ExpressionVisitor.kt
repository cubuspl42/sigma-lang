package com.github.cubuspl42.sigmaLang.core.concepts.visitors

import com.github.cubuspl42.sigmaLang.core.expressions.Expression

abstract class ExpressionVisitor {
    private val visitedExpressions = mutableSetOf<Expression>()

    fun visitOnce(expression: Expression) {
        if (expression in visitedExpressions) {
            return
        }

        visitedExpressions.add(expression)

        visit(expression)

        expression.subExpressions.forEach { visitOnce(it) }
    }

    abstract fun visit(expression: Expression)
}
