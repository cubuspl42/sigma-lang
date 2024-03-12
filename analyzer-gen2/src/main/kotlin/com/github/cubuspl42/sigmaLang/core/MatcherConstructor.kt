package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression

class MatcherConstructor(
    override val rawExpression: Expression,
) : ShadowExpression() {
    abstract class PatternBlock(
        val pattern: Pattern,
    ) {
        abstract fun makeResult(
            definitionBlock: LocalScope.DefinitionBlock,
        ): ShadowExpression
    }

    companion object {
        fun make(
            matched: ShadowExpression,
            patternBlocks: List<PatternBlock>,
            elseResult: ShadowExpression,
        ): ExpressionBuilder<MatcherConstructor> = object : ExpressionBuilder<MatcherConstructor>() {
            override fun build(buildContext: Expression.BuildContext): MatcherConstructor {
                val result = matched.bindToReference { matchedReference ->
                    SwitchExpression.make(
                        buildContext = buildContext,
                        caseBlocks = patternBlocks.map { patternBlock ->
                            val application = patternBlock.pattern.apply(matchedReference)

                            val result = application.definitionBlock.bindToReference {
                                patternBlock.makeResult(definitionBlock = it.asDefinitionBlock())
                            }

                            SwitchExpression.CaseBlock(
                                condition = application.condition,
                                result = result,
                            )
                        },
                        elseResult = elseResult,
                    )
                }

                return MatcherConstructor(
                    rawExpression = result.rawExpression,
                )
            }

        }
    }
}
