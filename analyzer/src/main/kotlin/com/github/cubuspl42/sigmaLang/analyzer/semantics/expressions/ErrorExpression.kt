package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.EvaluationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.NeverType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class ErrorExpression(
    override val term: ExpressionTerm?,
) : FirstOrderExpression() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val computedDiagnosedAnalysis = Computation.pure(
        DiagnosedAnalysis(
            inferredType = NeverType,
        )
    )

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = object : Thunk<Value>() {
        override fun evaluateDirectly(context: EvaluationContext): EvaluationOutcome<Value> {
            throw UnsupportedOperationException("Error expression cannot be evaluated")
        }
    }

    override val subExpressions: Set<Expression> = emptySet()
}
