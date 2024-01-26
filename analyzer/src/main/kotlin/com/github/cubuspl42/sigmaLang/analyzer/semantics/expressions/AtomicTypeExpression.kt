package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class AtomicTypeExpression(
    private val type: SpecificType,
    private val valueThunk: Thunk<Value>,
) : FirstOrderExpression() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ExpressionTerm? = null

    override val computedAnalysis: Computation<Analysis?> = Computation.pure(
        Analysis(
            typeInference = TypeInference(inferredType = type),
            directErrors = emptySet(),
        ),
    )

    override val subExpressions: Set<Expression> = emptySet()

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = valueThunk
}
