package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.CallStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.uncons

data class WhenTerm(
    val entries: List<ConditionalEntry>,
    val elseEntry: ExpressionTerm?,
) : ExpressionTerm {
    data class ConditionalEntry(
        val condition: ExpressionTerm,
        val result: ExpressionTerm,
    )

    companion object : Term.Builder<SigmaParser.WhenContext, WhenTerm>() {
        override fun build(
            ctx: SigmaParser.WhenContext,
        ): WhenTerm = WhenTerm(
            entries = ctx.whenConditionalEntry().map {
                ConditionalEntry(
                    condition = ExpressionTerm.build(it.condition),
                    result = ExpressionTerm.build(it.result),
                )
            },
            elseEntry = ctx.whenElseEntry()?.let { ExpressionTerm.build(it.result) },
        )

        override fun extract(parser: SigmaParser): SigmaParser.WhenContext = parser.`when`()
    }

    override fun transmute(): ExpressionStub {
        val ifExpression = ExpressionStub.ifFunction

        fun constructElseExpression(): ExpressionStub = elseEntry?.transmute() ?: CallStub.panicCall

        fun constructConditionalEntryExpression(
            conditionalEntry: ConditionalEntry,
            elseCase: ExpressionStub,
        ): ExpressionStub = ifExpression.call(
            condition = conditionalEntry.condition.transmute(),
            thenCase = conditionalEntry.result.transmute(),
            elseCase = elseCase,
        )

        fun constructEntryExpression(
            remainingEntries: List<ConditionalEntry>,
        ): ExpressionStub {
            val (head, tail) = remainingEntries.uncons() ?: return constructElseExpression()

            return constructConditionalEntryExpression(
                conditionalEntry = head,
                elseCase = constructEntryExpression(tail),
            )
        }

        return constructEntryExpression(
            remainingEntries = entries,
        )
    }
}
