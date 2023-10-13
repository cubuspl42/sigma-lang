package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

class SymbolLiteral(
    val value: Symbol,
    override val outerScope: StaticScope,
) : Expression() {
    override val term: ExpressionTerm? = null

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = SymbolType(value = value),
            ),
            directErrors = emptySet(),
        )
    }



    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = value.toThunk()
}
