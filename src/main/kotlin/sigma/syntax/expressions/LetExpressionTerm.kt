package sigma.syntax.expressions

import sigma.StaticTypeScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.syntax.SourceLocation
import sigma.values.tables.Scope
import sigma.semantics.types.Type

data class LetExpressionTerm(
    override val location: SourceLocation,
    val localScope: LocalScopeTerm,
    val result: ExpressionTerm,
) : ExpressionTerm() {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpressionTerm = LetExpressionTerm(
            location = SourceLocation.build(ctx),
            localScope = LocalScopeTerm.build(ctx.scope),
            result = ExpressionTerm.build(ctx.result),
        )
    }

    override fun dump(): String = "(let expression)"

    override fun validateAdditionally(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ) {
        localScope.validate(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        val innerValueScope = localScope.evaluateStatically(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        result.validate(
            typeScope = typeScope,
            valueScope = innerValueScope,
        )
    }

    override fun determineType(
        typeScope: StaticTypeScope,
        valueScope: StaticValueScope,
    ): Type {
        val innerValueScope = localScope.evaluateStatically(
            typeScope = typeScope,
            valueScope = valueScope,
        )

        return result.determineType(
            typeScope = typeScope,
            valueScope = innerValueScope,
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Thunk = result.evaluate(
        scope = localScope.evaluateDynamically(
            scope = scope,
        ),
    )
}
