package sigma.syntax.expressions

import sigma.SyntaxTypeScope
import sigma.SyntaxValueScope
import sigma.parser.antlr.SigmaParser.IsUndefinedCheckContext
import sigma.syntax.SourceLocation
import sigma.semantics.types.BoolType
import sigma.semantics.types.Type
import sigma.values.BoolValue
import sigma.values.UndefinedValue
import sigma.values.tables.Scope

data class IsUndefinedCheckTerm(
    override val location: SourceLocation,
    val argument: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: IsUndefinedCheckContext,
        ): IsUndefinedCheckTerm = IsUndefinedCheckTerm(
            location = SourceLocation.build(ctx),
            argument = ExpressionTerm.build(ctx),
        )
    }

    override fun determineType(
        typeScope: SyntaxTypeScope,
        valueScope: SyntaxValueScope,
    ): Type = BoolType

    override fun evaluate(
        scope: Scope,
    ): BoolValue {
        val argumentValue = argument.evaluate(scope = scope).toEvaluatedValue

        return BoolValue(
            value = argumentValue is UndefinedValue,
        )
    }

    override fun dump(): String = "(isUndefined)"
}
