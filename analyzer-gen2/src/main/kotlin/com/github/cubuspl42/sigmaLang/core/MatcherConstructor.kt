package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Expression

class MatcherConstructor(
    override val rawExpression: Expression,
) : ShadowExpression() {
    data class PatternBlock(
        @Suppress("PropertyName") val class_: ShadowExpression,
        val result: ShadowExpression,
    )

    companion object {
        fun make(
            matched: ShadowExpression,
            patternBlocks: List<PatternBlock>,
            elseResult: ShadowExpression,
        ): ExpressionBuilder<MatcherConstructor> = object : ExpressionBuilder<MatcherConstructor>() {
            override fun build(buildContext: Expression.BuildContext): MatcherConstructor {
                val result = LocalScope.Constructor.bindSingle(
                    expression = matched,
                    makeResult = { matchedReference ->
                        SwitchExpression.make(
                            buildContext = buildContext,
                            caseBlocks = patternBlocks.map { patternBlock ->
                                SwitchExpression.CaseBlock(
                                    condition = matchedReference.isA(
                                        class_ = patternBlock.class_,
                                    ).build(
                                        buildContext = buildContext,
                                    ),
                                    result = patternBlock.result,
                                )
                            },
                            elseResult = elseResult,
                        )
                    }
                ).build(
                    buildContext = buildContext,
                )

                return MatcherConstructor(
                    rawExpression = result.rawExpression,
                )
            }

        }
    }
}
