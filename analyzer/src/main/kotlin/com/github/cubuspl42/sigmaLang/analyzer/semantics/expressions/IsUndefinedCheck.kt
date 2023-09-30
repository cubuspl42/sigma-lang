package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckTerm

data class IsUndefinedCheck(
    override val outerScope: StaticScope,
    override val term: IsUndefinedCheckTerm,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: IsUndefinedCheckTerm,
        ): IsUndefinedCheck = IsUndefinedCheck(
            outerScope = outerScope,
            term = term,
            argument = Expression.build(
                outerScope = outerScope,
                term = term.argument,
            ),
        )
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val argumentAnalysis = compute(argument.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = BoolType,
                classifiedValue = argumentAnalysis.classifiedValue.transform { argumentValue ->
                    BoolValue(
                        value = argumentValue is UndefinedValue,
                    )
                },
            ),
            directErrors = emptySet(),
        )
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = argument.bind(
        dynamicScope = dynamicScope,
    ).thenJust { argumentValue ->
        BoolValue(
            value = argumentValue is UndefinedValue,
        )
    }

    override val subExpressions: Set<Expression> = setOf(argument)
}
