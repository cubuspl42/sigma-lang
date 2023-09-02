package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.LocalValueDefinitionBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionSourceTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm

data class LetExpression(
    override val outerScope: StaticScope,
    override val term: LetExpressionTerm,
    val definitionBlock: LocalValueDefinitionBlock,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            outerScope: StaticScope,
            term: LetExpressionTerm,
        ): LetExpression {
            val (definitionBlock, innerDeclarationScope) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = LocalValueDefinitionBlock.build(
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

    override val inferredType: Thunk<Type>
        get() = result.inferredType

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = result.bind(
        dynamicScope = definitionBlock.evaluate(
            dynamicScope = dynamicScope,
        ),
    )

    override val subExpressions: Set<Expression> by lazy {
        definitionBlock.subExpressions + result
    }

    override val errors: Set<SemanticError> by lazy {
        definitionBlock.errors + result.errors
    }
}
