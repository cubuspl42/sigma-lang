package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.FieldReadAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier

data class FieldReadSourceTerm(
    override val location: SourceLocation,
    override val subject: ExpressionTerm,
    override val fieldName: Identifier,
) : ExpressionSourceTerm(), FieldReadTerm {
    companion object {
        fun build(
            ctx: FieldReadAltContext,
        ): FieldReadSourceTerm = FieldReadSourceTerm(
            location = SourceLocation.build(ctx),
            subject = build(ctx.subject),
            fieldName = Identifier.of(ctx.fieldName.text),
        )
    }

    override fun dump(): String = "${subject.dump()}.${fieldName.name}"
}
