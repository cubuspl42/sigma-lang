package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Call
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext

data class FieldReadTerm(
    val subject: ReferenceTerm,
    val readFieldName: IdentifierTerm,
) : ExpressionTerm {
    companion object : Term.Builder<SigmaParser.FieldReadContext, FieldReadTerm>() {
        override fun build(
            ctx: SigmaParser.FieldReadContext,
        ): FieldReadTerm = FieldReadTerm(
            subject = ReferenceTerm.build(ctx.reference()),
            readFieldName = IdentifierTerm.build(ctx.readFieldName),
        )

        override fun extract(parser: SigmaParser): SigmaParser.FieldReadContext = parser.fieldRead()
    }

    override fun construct(context: ConstructionContext): Lazy<Expression> = Call.fieldRead(
        subjectLazy = subject.construct(context),
        readFieldName = readFieldName.construct(),
    )
}
