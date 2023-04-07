package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.parser.antlr.SigmaParser.AbstractionContext
import sigma.parser.antlr.SigmaParser.GenericParametersTupleContext
import sigma.parser.antlr.SigmaParser.IsUndefinedCheckContext
import sigma.syntax.SourceLocation
import sigma.syntax.typeExpressions.TupleTypeLiteral
import sigma.syntax.typeExpressions.TypeExpression
import sigma.semantics.types.BoolType
import sigma.semantics.types.UniversalFunctionType
import sigma.semantics.types.Type
import sigma.semantics.types.TypeVariable
import sigma.values.BoolValue
import sigma.values.Closure
import sigma.values.FixedStaticTypeScope
import sigma.values.Symbol
import sigma.values.UndefinedValue
import sigma.values.tables.Scope

data class IsUndefinedCheck(
    override val location: SourceLocation,
    val argument: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: IsUndefinedCheckContext,
        ): IsUndefinedCheck = IsUndefinedCheck(
            location = SourceLocation.build(ctx),
            argument = Expression.build(ctx),
        )
    }

    override fun validateAndInferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
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
