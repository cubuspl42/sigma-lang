package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.uncons

data class WhenTerm(
    val caseBlocks: List<CaseBlock>,
    val elseBlock: ExpressionTerm?,
) : ExpressionTerm {
    data class CaseBlock(
        val condition: ExpressionTerm,
        val result: ExpressionTerm,
    ): Wrappable {
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

    override fun transmute(): ExpressionStub<Expression> {
        val ifExpression = ExpressionBuilder.ifFunction

        fun constructElseExpression(): ExpressionStub<Expression> =
            elseBlock?.transmute() ?: ExpressionBuilder.panicFunction.map {
                it.rawExpression.call(
                    passedArgument = UnorderedTupleConstructor.Empty,
                )
            }.asStub()

        fun constructConditionalEntryExpression(
            caseBlock: CaseBlock,
            elseCaseStub: ExpressionStub<Expression>,
        ): ExpressionStub<Expression> = ExpressionStub.map3Unpacked(
            caseBlock.condition.transmute(),
            caseBlock.result.transmute(),
            elseCaseStub,
        ) { condition, result, elseCase ->
            ifExpression.map { ifFunction ->
                ifFunction.call(
                    condition = condition,
                    thenCase = result,
                    elseCase = elseCase,
                )
            }
        }

        fun constructEntryExpression(
            remainingEntries: List<CaseBlock>,
        ): ExpressionStub<Expression> {
            val (head, tail) = remainingEntries.uncons() ?: return constructElseExpression()

            return constructConditionalEntryExpression(
                caseBlock = head,
                elseCaseStub = constructEntryExpression(tail),
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
