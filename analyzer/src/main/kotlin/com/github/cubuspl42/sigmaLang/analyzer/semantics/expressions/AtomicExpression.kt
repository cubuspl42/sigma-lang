package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class AtomicExpression(
    private val type: SpecificType,
    private val valueThunk: Thunk<Value>,
) : FirstOrderExpression() {
    companion object {
        fun forType(
            type: TypeAlike,
        ): AtomicExpression = AtomicExpression(
            type = TypeType,
            value = type.asValue,
        )
    }

    constructor(
        type: SpecificType,
        value: Value,
    ) : this(
        type = type,
        valueThunk = Thunk.pure(value),
    )

    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ExpressionTerm? = null

    override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?> = Computation.pure(
        DiagnosedAnalysis(
            analysis = Analysis(inferredType = type),
            directErrors = emptySet(),
        ),
    )

    override val subExpressions: Set<Expression> = emptySet()

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = valueThunk
}
