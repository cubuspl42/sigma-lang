package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IsUndefinedCheckTerm

abstract class IsUndefinedCheck : Expression() {
    abstract override val term: IsUndefinedCheckTerm

    abstract val argument: Expression

    companion object {
        fun build(
            context: BuildContext,
            term: IsUndefinedCheckTerm,
        ): Stub<IsUndefinedCheck> = object : Stub<IsUndefinedCheck> {
            override val resolved: IsUndefinedCheck by lazy {
                object : IsUndefinedCheck() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: IsUndefinedCheckTerm = term

                    override val argument: Expression by lazy {
                        Expression.build(
                            context = context,
                            term = term.argument,
                        ).resolved
                    }
                }
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

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = argument.bind(
        dynamicScope = dynamicScope,
    ).thenJust { argumentValue ->
        BoolValue(
            value = argumentValue is UndefinedValue,
        )
    }

    override val subExpressions: Set<Expression>
        get() = setOf(argument)
}
