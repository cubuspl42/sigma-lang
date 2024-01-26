package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class AtomicExpression : FirstOrderExpression() {
    abstract val type: Type

    abstract val valueThunk: Thunk<Value>

    companion object {
        fun forType(
            type: TypeAlike,
        ): AtomicExpression = AtomicExpression(
            type = TypeType,
            value = type.asValue,
        )
    }

    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ExpressionTerm? = null

    override val computedAnalysis: Computation<Analysis?> by lazy {
        Computation.pure(
            Analysis(
                typeInference = TypeInference(inferredType = type),
                directErrors = emptySet(),
            ),
        )
    }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = valueThunk
}

@JvmName("AtomicExpressionFromValueThunk")
fun AtomicExpression(
    type: SpecificType,
    valueThunk: Thunk<Value>,
): AtomicExpression = object : AtomicExpression() {
    override val type: SpecificType = type

    override val valueThunk: Thunk<Value> = valueThunk
}

@JvmName("AtomicExpressionFromValue")
fun AtomicExpression(
    type: SpecificType,
    value: Value,
): AtomicExpression = object : AtomicExpression() {
    override val type: SpecificType = type

    override val valueThunk: Thunk<Value> = Thunk.pure(value)
}
