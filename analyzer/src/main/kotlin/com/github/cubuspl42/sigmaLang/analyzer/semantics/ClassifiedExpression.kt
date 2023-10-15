package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration

sealed class ClassifiedExpression {
    abstract val expression: Expression
    abstract fun bind(dynamicScope: DynamicScope): Thunk<Value>
}

data class ConstExpression(
    override val expression: Expression,
    val valueThunk: Thunk<Value>,
) : ClassifiedExpression() {
    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = valueThunk
}

data class VariableExpression(
    override val expression: Expression,
    /**
     * A non-empty set of reachable declarations
     */
    val reachableDeclarations: Set<Declaration>,
) : ClassifiedExpression() {
    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = expression.bindDirectly(
        dynamicScope = dynamicScope,
    )
}
