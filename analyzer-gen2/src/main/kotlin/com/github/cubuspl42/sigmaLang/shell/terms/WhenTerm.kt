package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.map
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.utils.uncons

data class WhenTerm(
    val entries: List<ConditionalEntry>,
    val elseEntry: ExpressionTerm?,
) : ExpressionTerm {
    data class ConditionalEntry(
        val condition: ExpressionTerm,
        val result: ExpressionTerm,
    ): Wrappable {
        override fun wrap(): Value = UnorderedTuple(
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

    override fun transmute(): ExpressionStub<ShadowExpression> {
        val ifExpression = ExpressionBuilder.ifFunction

        fun constructElseExpression(): ExpressionStub<ShadowExpression> =
            elseEntry?.transmute() ?: ExpressionBuilder.panicFunction.map {
                it.rawExpression.call(
                    passedArgument = UnorderedTupleConstructor.Empty,
                )
            }.asStub()

        fun constructConditionalEntryExpression(
            conditionalEntry: ConditionalEntry,
            elseCaseStub: ExpressionStub<ShadowExpression>,
        ): ExpressionStub<ShadowExpression> = ExpressionStub.map3Unpacked(
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
        ): ExpressionStub<ShadowExpression> {
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

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("entries") to lazyOf(entries.wrap()),
            Identifier.of("elseEntry") to lazyOf(elseEntry.wrapOrNil()),
        )
    )
}
