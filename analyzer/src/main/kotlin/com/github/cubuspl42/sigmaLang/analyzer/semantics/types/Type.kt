package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression

sealed class Type : TypeAlike() {
    interface VariableExpressionBuildingContext {
        companion object {
            fun <A> looped(
                build: (VariableExpressionBuildingContext) -> Pair<A, VariableExpressionBuildingContext>,
            ): Pair<A, VariableExpressionBuildingContext> = object : VariableExpressionBuildingContext {
                val result = build(this)

                private val resultContext = result.second

                override fun getMapped(type: Type): Expression? = resultContext.getMapped(type = type)
            }.result
        }

        object Empty : VariableExpressionBuildingContext {
            override fun getMapped(type: Type): Expression? = null
        }

        fun getMapped(type: Type): Expression?

        fun extended(
            key: Type,
            innerExpression: Expression,
        ): VariableExpressionBuildingContext = object : VariableExpressionBuildingContext {
            override fun getMapped(
                type: Type,
            ): Expression? = if (type == key) {
                innerExpression
            } else {
                this@VariableExpressionBuildingContext.getMapped(type = type)
            }
        }
    }


}
