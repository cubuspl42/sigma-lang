package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
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
            context: BuildContext,
            term: IsUndefinedCheckTerm,
        ): Stub<IsUndefinedCheck> = object : Stub<IsUndefinedCheck> {
            override val resolved: IsUndefinedCheck by lazy {
                IsUndefinedCheck(
                    outerScope = context.outerScope,
                    term = term,
                    argument = Expression.build(
                        context = context,
                        term = term.argument,
                    ).resolved,
                )
            }
        }
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = BoolType,
            ),
            directErrors = emptySet(),
        )
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        argument.classifiedValue.transform { argumentValue ->
            BoolValue(
                value = argumentValue is UndefinedValue,
            )
        }
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
