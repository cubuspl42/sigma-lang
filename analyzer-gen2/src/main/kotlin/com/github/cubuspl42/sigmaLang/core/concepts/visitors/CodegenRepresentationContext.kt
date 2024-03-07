package com.github.cubuspl42.sigmaLang.core.concepts.visitors

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.squareup.kotlinpoet.typeNameOf

class CodegenRepresentationContext : ExpressionVisitor() {
    companion object {
        val valueTypeName = typeNameOf<Value>()
    }

    private var nextId = 0

    private val representationByExpression: MutableMap<Expression, Expression.CodegenRepresentation> =
        mutableMapOf()

    fun getRepresentation(expression: Expression): Expression.CodegenRepresentation =
        representationByExpression[expression] ?: throw IllegalStateException("No representation for $expression")

    fun generateUniqueName(prefix: String): String = "${prefix}${nextId++}"

    override fun visit(expression: Expression) {
        representationByExpression[expression] = expression.buildCodegenRepresentation(
            context = this,
        )
    }
}
