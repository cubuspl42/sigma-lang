package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.builtin.BuiltinModule
import com.github.cubuspl42.sigmaLang.utils.uncons

class SwitchExpression(
    val rootExpression: Expression,
) : ShadowExpression() {
    data class CaseBlock(
        val condition: Expression,
        val result: Expression,
    )

    companion object {
        fun make(
            caseBlocks: List<CaseBlock>,
            elseResult: Expression,
        ): SwitchExpression {
            val ifFunction = BuiltinModuleReference.ifFunction

            fun constructElseExpression(): Expression = elseResult

            fun constructConditionalEntryExpression(
                caseBlock: CaseBlock,
                tailExpression: Expression,
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

    override val rawExpression: Expression
        get() = rootExpression
}
