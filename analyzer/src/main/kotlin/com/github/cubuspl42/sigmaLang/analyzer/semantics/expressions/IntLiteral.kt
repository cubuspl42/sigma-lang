package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions


import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm

data class IntLiteral(
    override val term: IntLiteralTerm,
    override val outerScope: StaticScope,
) : Expression() {
    companion object {
        fun build(
            context: BuildContext,
            term: IntLiteralTerm,
        ): Stub<IntLiteral> = object : Stub<IntLiteral> {
            override val resolved: IntLiteral by lazy {
                IntLiteral(
                    outerScope = context.outerScope,
                    term = term,
                )
            }
        }
    }

    val value: IntValue
        get() = term.value

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = IntLiteralType(
                    value = value,
                ),
            ),
            directErrors = emptySet(),
        )
    }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = value.toThunk()
}
