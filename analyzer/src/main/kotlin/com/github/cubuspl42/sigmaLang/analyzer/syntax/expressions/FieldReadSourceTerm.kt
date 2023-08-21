package com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser.FieldReadAltContext
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol

data class FieldReadSourceTerm(
    override val location: SourceLocation,
    val subject: ExpressionSourceTerm,
    val fieldName: Symbol,
) : ExpressionSourceTerm() {
    companion object {
        fun build(
            ctx: FieldReadAltContext,
        ): FieldReadSourceTerm = FieldReadSourceTerm(
            location = SourceLocation.build(ctx),
            subject = build(ctx.subject),
            fieldName = Symbol.of(ctx.fieldName.text),
        )
    }

    override fun dump(): String = "${subject.dump()}.${fieldName.name}"
}
