package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions


import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StringValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.toThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.StringType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.StringLiteralTerm

data class StringLiteral(
    override val term: StringLiteralTerm,
    override val outerScope: StaticScope,
) : Expression() {
    companion object {
        fun build(
            context: BuildContext,
            term: StringLiteralTerm,
        ): Stub<StringLiteral> = object : Stub<StringLiteral> {
            override val resolved: StringLiteral by lazy {
                StringLiteral(
                    outerScope = context.outerScope,
                    term = term,
                )
            }
        }
    }

    val value: StringValue
        get() = term.value

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        DiagnosedAnalysis(
            analysis = Analysis(
                inferredType = StringType,
            ),
            directErrors = emptySet(),
        )
    }

    override val classifiedValue: ClassificationContext<Value> = ConstClassificationContext.pure(value)

    override val subExpressions: Set<Expression> = emptySet()

    override fun bind(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = value.toThunk()
}
