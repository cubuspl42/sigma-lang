package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.utils.uncons

class SwitchExpression(
    val rootExpression: ShadowExpression,
) : ShadowExpression() {
    class Builder(
        private val caseBlocks: List<CaseBlockBuilder>,
        private val elseBlock: ElseBlockBuilder,
    ) : ExpressionBuilder<SwitchExpression>() {
        data class CaseBlockBuilder(
            val condition: ExpressionBuilder<ShadowExpression>,
            val result: ExpressionBuilder<ShadowExpression>,
        )

        data class ElseBlockBuilder(
            val result: ExpressionBuilder<ShadowExpression>,
        )

        override fun build(buildContext: Expression.BuildContext): SwitchExpression {
            val ifFunction = ExpressionBuilder.ifFunction.build(buildContext = buildContext)

            fun constructElseExpression(): Expression = elseBlock.result.buildRaw(
                buildContext = buildContext,
            )

            fun constructConditionalEntryExpression(
                caseBlock: CaseBlockBuilder,
                tailExpression: Expression,
            ): Call {
                val condition = caseBlock.condition.build(buildContext = buildContext)
                val result = caseBlock.result.build(buildContext = buildContext)

                return ifFunction.call(
                    condition = condition,
                    thenCase = result,
                    elseCase = tailExpression,
                )
            }

            fun constructSwitchExpression(
                remainingBlocks: List<CaseBlockBuilder>,
            ): Expression {
                val (headCaseBlock, tailCaseBlocks) = remainingBlocks.uncons() ?: return constructElseExpression()

                return constructConditionalEntryExpression(
                    caseBlock = headCaseBlock,
                    tailExpression = constructSwitchExpression(tailCaseBlocks),
                )
            }

            return SwitchExpression(
                rootExpression = constructSwitchExpression(
                    remainingBlocks = caseBlocks,
                ),
            )
        }
    }

    data class CaseBlock(
        val condition: ShadowExpression,
        val result: ShadowExpression,
    )

    companion object {
        fun make(
            buildContext: Expression.BuildContext,
            caseBlocks: List<CaseBlock>,
            elseResult: ShadowExpression,
        ): SwitchExpression {
            val ifFunction = ExpressionBuilder.ifFunction.build(buildContext = buildContext)

            fun constructElseExpression(): ShadowExpression = elseResult

            fun constructConditionalEntryExpression(
                caseBlock: CaseBlock,
                tailExpression: ShadowExpression,
            ): Call {
                val condition = caseBlock.condition
                val result = caseBlock.result

                return ifFunction.call(
                    condition = condition,
                    thenCase = result,
                    elseCase = tailExpression,
                )
            }

            fun constructSwitchExpression(
                remainingBlocks: List<CaseBlock>,
            ): ShadowExpression {
                val (headCaseBlock, tailCaseBlocks) = remainingBlocks.uncons() ?: return constructElseExpression()

                return constructConditionalEntryExpression(
                    caseBlock = headCaseBlock,
                    tailExpression = constructSwitchExpression(tailCaseBlocks),
                )
            }

            return SwitchExpression(
                rootExpression = constructSwitchExpression(
                    remainingBlocks = caseBlocks,
                ),
            )
        }
    }

    override val rawExpression: Expression
        get() = rootExpression.rawExpression
}