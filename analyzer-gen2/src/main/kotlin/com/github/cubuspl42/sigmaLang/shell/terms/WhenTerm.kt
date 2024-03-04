package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
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

    override fun construct(context: ConstructionContext): Lazy<Expression> {
        val ifExpression = context.referIf()

        fun constructElseExpression(): Lazy<Expression> = elseEntry?.construct(context) ?: context.buildPanicCall()

        fun constructConditionalEntryExpression(
            conditionalEntry: ConditionalEntry,
            elseCase: Lazy<Expression>,
        ): Lazy<Expression> = ifExpression.constructCall(
            condition = conditionalEntry.condition.construct(context = context),
            thenCase = conditionalEntry.result.construct(context = context),
            elseCase = elseCase,
        )

        fun constructEntryExpression(
            remainingEntries: List<ConditionalEntry>,
        ): Lazy<Expression> {
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
