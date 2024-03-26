package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.shell.stubs.map

data class FieldReadTerm(
    val subject: ExpressionTerm,
    val readFieldName: Identifier,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.FieldReadCallableExpressionAltContext,
        ): FieldReadTerm = FieldReadTerm(
            subject = ExpressionTerm.build(ctx.callee()),
            readFieldName = IdentifierTerm.build(ctx.readFieldName).toIdentifier(),
        )
    }

    override fun transmute() = subject.transmute().map {
        it.readField(fieldName = readFieldName)
    }

    override fun wrap() = UnorderedTupleValue(
        valueByKey = mapOf(
            Identifier.of("subject") to lazyOf(subject.wrap()),
            Identifier.of("readFieldName") to lazyOf(readFieldName),
        ),
    )
}
