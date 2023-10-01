package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.UndefinedValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableDefinitionBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val outerScope: StaticScope,
    override val term: LetExpressionTerm,
    val definitionBlock: VariableDefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val (definitionBlock, innerDeclarationScope) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = VariableDefinitionBlock.build(
                    outerDeclarationScope = innerDeclarationScopeLooped,
                    definitions = term.definitions,
                )

                val innerDeclarationScope = definitionBlock.chainWith(
                    outerScope = outerScope,
                )

                return@looped Pair(
                    definitionBlock,
                    innerDeclarationScope,
                )
            }

            return LetExpression(
                outerScope = outerScope,
                term = term,
                definitionBlock = definitionBlock,
                result = Expression.build(
                    outerScope = innerDeclarationScope,
                    term = term.result,
                ),
            )
        }
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val resultAnalysis = compute(result.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        DiagnosedAnalysis(
            analysis = resultAnalysis,
            directErrors = emptySet(),
        )
    }

    override val classifiedValue: ClassificationContext<Value>
        get() = result.classifiedValue

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = result.bind(
        dynamicScope = definitionBlock.evaluate(
            outerScope = dynamicScope,
        ),
    )

    override val subExpressions: Set<Expression> by lazy {
        definitionBlock.subExpressions + result
    }
}
