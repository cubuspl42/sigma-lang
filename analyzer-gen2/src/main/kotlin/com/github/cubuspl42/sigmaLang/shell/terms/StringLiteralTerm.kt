package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.StringLiteral
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.stubs.asStub

data class StringLiteralTerm(
    override val value: StringPrimitive,
) : PrimitiveLiteralTerm() {
    companion object : Term.Builder<SigmaParser.StringLiteralContext, StringLiteralTerm>() {
        override fun build(
            ctx: SigmaParser.StringLiteralContext,
        ): StringLiteralTerm {
            // Drop the quotes
            val value = ctx.text.drop(1).dropLast(1)

            return StringLiteralTerm(
                value = StringPrimitive(value = value),
            )
        }

        override fun extract(
            parser: SigmaParser,
        ): SigmaParser.StringLiteralContext = parser.stringLiteral()
    }

    override fun transmute() = StringLiteral(
        value = value,
    ).asStub()

    override fun wrap(): Value = UnorderedTuple(
        valueByKey = mapOf(
            Identifier.of("value") to lazyOf(value),
        )
    )
}