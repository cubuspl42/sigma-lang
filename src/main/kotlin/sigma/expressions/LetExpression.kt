package sigma.expressions

import sigma.StaticScope
import sigma.Thunk
import sigma.values.tables.LoopedScope
import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.values.tables.Scope
import sigma.types.Type
import sigma.values.LoopedStaticValueScope

data class LetExpression(
    override val location: SourceLocation,
    val declarations: List<Declaration>,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            ctx: LetExpressionContext,
        ): LetExpression = LetExpression(
            location = SourceLocation.build(ctx),
            declarations = ctx.scope.declaration().map {
                Declaration.build(it)
            },
            result = Expression.build(ctx.result),
        )
    }

    override fun dump(): String = "(let expression)"

    override fun inferType(
        scope: StaticScope,
    ): Type {
        val innerValueScope = buildValueScope(scope = scope)

        return result.inferType(
            scope = scope.copy(
                valueScope = innerValueScope,
            ),
        )
    }

    override fun validate(
        scope: StaticScope,
    ) {
        buildValueScope(scope = scope).validate()
    }

    private fun buildValueScope(
        scope: StaticScope,
    ) = LoopedStaticValueScope(
        context = scope,
        declarations = declarations,
    )

    override fun evaluate(
        scope: Scope,
    ): Thunk {
        val innerScope = LoopedScope(
            context = scope,
            declarations = declarations.associate {
                it.name to it.value
            },
        )

        return result.evaluate(
            scope = innerScope,
        )
    }
}
