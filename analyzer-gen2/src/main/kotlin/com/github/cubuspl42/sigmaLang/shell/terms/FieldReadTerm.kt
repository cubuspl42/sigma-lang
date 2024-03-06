package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.CallStub
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub

data class FieldReadTerm(
    val subject: ExpressionTerm,
    val readFieldName: IdentifierTerm,
) : ExpressionTerm {
    companion object {
        fun build(
            ctx: SigmaParser.FieldReadCallableExpressionAltContext,
        ): FieldReadTerm = FieldReadTerm(
            subject = ExpressionTerm.build(ctx.callee()),
            readFieldName = IdentifierTerm.build(ctx.readFieldName),
        )
    }

    override fun transmute() = CallStub.fieldRead(
        subjectStub = subject.transmute(),
        fieldName = readFieldName.transmute(),
    )
}
