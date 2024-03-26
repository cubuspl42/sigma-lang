package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.bindToReference

class MatcherConstructor(
    override val rawExpression: Expression,
) : ShadowExpression() {
    abstract class PatternBlock(
        val pattern: Pattern,
    ) {
        abstract fun makeResult(
            definitionBlock: LocalScope.DefinitionBlock,
        ): Expression
    }

    companion object {
        fun make(
            matched: Expression,
            patternBlocks: List<PatternBlock>,
            elseResult: Expression,
        ): ExpressionBuilder<MatcherConstructor> = object : ExpressionBuilder<MatcherConstructor>() {
            override fun build(buildContext: Expression.BuildContext): MatcherConstructor {
                val result = matched.bindToReference { matchedReference ->
                    SwitchExpression.make(
                        buildContext = buildContext,
                        caseBlocks = patternBlocks.map { patternBlock ->
                            val application = patternBlock.pattern.apply(matchedReference)

                            val result = application.definitionBlock.rawExpression.bindToReference {
                                patternBlock.makeResult(definitionBlock = it.asDefinitionBlock())
                            }

                            SwitchExpression.CaseBlock(
                                condition = application.condition,
                                result = result,
                            )
                        },
                        elseResult = elseResult,
                    ).rawExpression
                }

                return MatcherConstructor(
                    rawExpression = result,
                )
            }

        }
    }
}
