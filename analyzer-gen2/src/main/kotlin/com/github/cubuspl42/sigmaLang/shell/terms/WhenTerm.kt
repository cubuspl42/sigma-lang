package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
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

    override fun transmute(): ExpressionStub<*> {
        val ifExpression = ExpressionStub.ifFunction

        fun constructElseExpression(): ExpressionStub<*> =
            elseEntry?.transmute() ?: ExpressionStub.panicFunction.map {
                it.rawExpression.call(
                    passedArgument = UnorderedTupleConstructor.Empty,
                )
            }.asStub()

        fun constructConditionalEntryExpression(
            conditionalEntry: ConditionalEntry,
            elseCaseStub: ExpressionStub<*>,
        ): ExpressionStub<*> = ExpressionStub.map3Unpacked(
            conditionalEntry.condition.transmute(),
            conditionalEntry.result.transmute(),
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
            remainingEntries: List<ConditionalEntry>,
        ): ExpressionStub<*> {
            val (head, tail) = remainingEntries.uncons() ?: return constructElseExpression()

            return constructConditionalEntryExpression(
                conditionalEntry = head,
                elseCaseStub = constructEntryExpression(tail),
            )
        }

        return constructEntryExpression(
            remainingEntries = entries,
        )
    }
}
