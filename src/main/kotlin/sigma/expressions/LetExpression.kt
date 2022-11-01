package sigma.expressions

import sigma.StaticScope
import sigma.StaticValueScope
import sigma.Thunk
import sigma.values.tables.LoopedScope
import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.values.tables.Scope
import sigma.types.Type
import sigma.values.LoopedStaticValueScope

data class LetExpression(
    val declarations: List<Declaration>,
    val result: Expression,
) : Expression() {
    companion object {
        fun build(
            let: LetExpressionContext,
        ): LetExpression = LetExpression(
            declarations = let.scope.declaration().map {
                Declaration.build(it)
            },
            result = Expression.build(let.result),
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
