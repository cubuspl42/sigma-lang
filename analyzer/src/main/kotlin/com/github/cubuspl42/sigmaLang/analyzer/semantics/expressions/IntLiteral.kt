package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions


import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.IntLiteralType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.IntLiteralTerm

data class IntLiteral(
    val value: IntValue,
    override val outerScope: StaticScope = StaticScope.Empty,
) : FirstOrderExpression() {
    companion object {
        fun of(value: Long): IntLiteral = IntLiteral(
            value = IntValue(value = value),
        )

        fun build(
            context: BuildContext,
            term: IntLiteralTerm,
        ): Stub<IntLiteral> = object : Stub<IntLiteral> {
            override val resolved: IntLiteral by lazy {
                IntLiteral(
                    outerScope = context.outerScope,
                    value = term.value,
                )
            }
        }
    }

    override val term: ExpressionTerm? = null

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

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = value.toThunk()
}
