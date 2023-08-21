package sigma.syntax.expressions

import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol

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
