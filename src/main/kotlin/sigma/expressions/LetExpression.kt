package sigma.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.values.tables.Scope
import sigma.types.Type

data class LetExpression(
    override val location: SourceLocation,
    val localScope: LocalScope,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpression = LetExpression(
            location = SourceLocation.build(ctx),
            localScope = LocalScope.build(ctx.scope),
            result = Expression.build(ctx.result),
        )
    }

    override fun dump(): String = "(let expression)"

    override fun inferType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type = result.inferType(
        typeScope = typeScope,
        valueScope = localScope.evaluateStatically(
            typeScope = typeScope,
            valueScope = valueScope,
        ),
    )

    override fun evaluate(
        scope: Scope,
    ): Thunk = result.evaluate(
        scope = localScope.evaluateDynamically(
            scope = scope,
        ),
    )
}
