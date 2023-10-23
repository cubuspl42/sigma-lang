package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionTerm

abstract class IfExpression : FirstOrderExpression() {
    abstract override val term: IfExpressionTerm

    abstract val guard: Expression

    abstract val trueBranch: Expression

    abstract val falseBranch: Expression

    companion object {
        fun build(
            context: BuildContext,
            term: IfExpressionTerm,
        ): Stub<IfExpression> = object : Stub<IfExpression> {
            override val resolved: IfExpression by lazy {
                object : IfExpression() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: IfExpressionTerm = term

                    override val guard: Expression by lazy {
                        build(
                            context = context,
                            term = term.guard,
                        ).resolved
                    }

                    override val trueBranch: Expression by lazy {
                        build(
                            context = context,
                            term = term.trueBranch,
                        ).resolved
                    }

                    override val falseBranch: Expression by lazy {
                        build(
                            context = context,
                            term = term.falseBranch,
                        ).resolved
                    }
                }
            }
        }
    }

    data class InvalidGuardError(
        override val location: SourceLocation? = null,
        val actualType: SpecificType,
    ) : SemanticError {
        override fun dump(): String = "$location: Invalid guard type: ${actualType.dump()} (should be: Bool)"
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val guardAnalysis = compute(guard.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val trueBranchAnalysis = compute(trueBranch.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val falseBranchAnalysis = compute(falseBranch.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val guardError = when (val inferredGuardType = guardAnalysis.inferredType as SpecificType) {
            is BoolType -> null
            else -> InvalidGuardError(
                location = guard.location,
                actualType = inferredGuardType,
            )
        }

        val inferredTrueType = trueBranchAnalysis.inferredType as SpecificType
        val inferredFalseType = falseBranchAnalysis.inferredType as SpecificType
        val inferredResultType = inferredTrueType.findLowestCommonSupertype(inferredFalseType)

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = inferredResultType,
            ),
            directErrors = setOfNotNull(guardError),
        )
    }

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = guard.bind(
        dynamicScope = dynamicScope,
    ).thenDo { guardValue ->
        if (guardValue !is BoolValue) throw IllegalArgumentException("Guard value $guardValue is not a boolean")

        if (guardValue.value) {
            trueBranch.bind(
                dynamicScope = dynamicScope,
            )
        } else {
            falseBranch.bind(
                dynamicScope = dynamicScope,
            )
        }
    }

    override val subExpressions: Set<Expression>
        get() = setOf(guard, trueBranch, falseBranch)
}
