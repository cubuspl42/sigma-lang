package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IfExpressionTerm

class IfExpression(
    override val outerScope: StaticScope,
    override val term: IfExpressionTerm,
    val guard: Expression,
    val trueBranch: Expression,
    val falseBranch: Expression,
) : Expression() {
    companion object {
        fun build(
            context: BuildContext,
            term: IfExpressionTerm,
        ): Stub<IfExpression> = object : Stub<IfExpression> {
            override val resolved: IfExpression by lazy {
                IfExpression(
                    outerScope = context.outerScope,
                    term = term,
                    guard = build(
                        context = context,
                        term = term.guard,
                    ).resolved,
                    trueBranch = build(
                        context = context,
                        term = term.trueBranch,
                    ).resolved,
                    falseBranch = build(
                        context = context,
                        term = term.falseBranch,
                    ).resolved,
                )
            }
        }
    }

    data class InvalidGuardError(
        override val location: SourceLocation?,
        val actualType: MembershipType,
    ) : SemanticError {
        override fun dump(): String = "$location: Invalid guard type: ${actualType.dump()} (should be: Bool)"
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val guardAnalysis = compute(guard.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val trueBranchAnalysis = compute(trueBranch.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val falseBranchAnalysis = compute(falseBranch.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val guardError = when (val inferredGuardType = guardAnalysis.inferredType) {
            is BoolType -> null
            else -> InvalidGuardError(
                location = guard.location,
                actualType = inferredGuardType,
            )
        }

        val inferredTrueType = trueBranchAnalysis.inferredType
        val inferredFalseType = falseBranchAnalysis.inferredType
        val inferredResultType = inferredTrueType.findLowestCommonSupertype(inferredFalseType)

        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = inferredResultType,
            ),
            directErrors = setOfNotNull(guardError),
        )
    }

    override val classifiedValue: ClassificationContext<Value> by lazy {
        ClassificationContext.transform3(
            guard.classifiedValue,
            trueBranch.classifiedValue,
            falseBranch.classifiedValue,
        ) { guardValue, trueValue, falseValue ->
            if (guardValue !is BoolValue) throw IllegalArgumentException("Guard value $guardValue is not a boolean")

            Thunk.pure(
                if (guardValue.value) trueValue else falseValue
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = guard.bind(
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

    override val subExpressions: Set<Expression> = setOf(guard, trueBranch, falseBranch)
}
