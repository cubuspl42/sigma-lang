package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.VariableDefinitionBlock
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.LetExpressionTerm

data class LetExpression(
    val definitionBlock: VariableDefinitionBlock,
    val resultStub: Stub<Expression>,
) {
    companion object {
        fun build(
            context: Expression.BuildContext,
            term: LetExpressionTerm,
        ): LetExpression {
            val (letExpression, _) = StaticScope.looped { innerDeclarationScopeLooped ->
                val definitionBlock = VariableDefinitionBlock.build(
                    context = context.copy(
                        outerScope = innerDeclarationScopeLooped,
                    ),
                    definitions = term.definitions,
                )

                val innerDeclarationScope = definitionBlock.chainWith(
                    outerScope = context.outerScope,
                )

                val resultStub = Expression.build(
                    context = context.copy(
                        outerScope = innerDeclarationScope,
                    ),
                    term = term.result,
                )

                val letExpression = LetExpression(
                    definitionBlock = definitionBlock,
                    resultStub = resultStub,
                )

                Pair(
                    letExpression,
                    innerDeclarationScope,
                )
            }


            return letExpression
        }
    }
}
