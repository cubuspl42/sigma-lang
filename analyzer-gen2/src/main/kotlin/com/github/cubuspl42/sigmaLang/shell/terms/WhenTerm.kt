package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.TransmutationContext
import com.github.cubuspl42.sigmaLang.utils.uncons

data class WhenTerm(
    val caseBlocks: List<CaseBlock>,
    val elseBlock: ExpressionTerm?,
) : ExpressionTerm {
    data class CaseBlock(
        val condition: ExpressionTerm,
        val result: ExpressionTerm,
    ) : Wrappable {
        override fun wrap(): Value = UnorderedTupleValue(
            valueByKey = mapOf(
                Identifier.of("condition") to lazyOf(condition.wrap()),
                Identifier.of("result") to lazyOf(result.wrap()),
            ),
        )
    }

    companion object : Term.Builder<SigmaParser.WhenContext, WhenTerm>() {
        override fun build(
            ctx: SigmaParser.WhenContext,
        ): WhenTerm = WhenTerm(
            caseBlocks = ctx.whenConditionalEntry().map {
                CaseBlock(
                    condition = ExpressionTerm.build(it.condition),
                    result = ExpressionTerm.build(it.result),
                )
            },
            elseBlock = ctx.whenElseEntry()?.let { ExpressionTerm.build(it.result) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.WhenContext = parser.`when`()
    }

    override fun transmute(context: TransmutationContext): Expression {
        fun constructElseExpression(): Expression = elseBlock?.transmute(
            context = context,
        ) ?: BuiltinModuleReference.panicFunction.call()

        fun constructConditionalEntryExpression(
            caseBlock: CaseBlock,
            elseCase: Expression,
        ): Expression {
            val conditionExpression = caseBlock.condition.transmute(
                context = context,
            )

            val resultExpression = caseBlock.result.transmute(
                context = context,
            )

            return BuiltinModuleReference.ifFunction.call(
                condition = conditionExpression,
                thenCase = resultExpression,
                elseCase = elseCase,
            )
        }

        fun constructEntryExpression(
            remainingEntries: List<CaseBlock>,
        ): Expression {
            val (head, tail) = remainingEntries.uncons() ?: return constructElseExpression()

            return constructConditionalEntryExpression(
                caseBlock = head,
                elseCase = constructEntryExpression(tail),
            )
        }

        return constructEntryExpression(
            remainingEntries = caseBlocks,
        )
    }

    override fun wrap(): Value = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("entries") to lazyOf(caseBlocks.wrap()),
            Identifier.of("elseEntry") to lazyOf(elseBlock.wrapOrNil()),
        )
    )
}
