package sigma.syntax.expressions

import sigma.evaluation.Thunk
import sigma.parser.antlr.SigmaParser.FieldReadAltContext
import sigma.syntax.SourceLocation
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.tables.DictTable
import sigma.evaluation.scope.Scope

data class FieldReadTerm(
    override val location: SourceLocation,
    val subject: ExpressionTerm,
    val fieldName: Symbol,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: FieldReadAltContext,
        ): FieldReadTerm = FieldReadTerm(
            location = SourceLocation.build(ctx),
            subject = build(ctx.subject),
            fieldName = Symbol.of(ctx.fieldName.text),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val subjectValue = subject.evaluate(scope = scope).toEvaluatedValue

        if (subjectValue !is DictTable) throw IllegalStateException("Subject $subjectValue is not a dict")

        val value = subjectValue.apply(fieldName)

        return value
    }

    override fun dump(): String = "${subject.dump()}.${fieldName.name}"
}
