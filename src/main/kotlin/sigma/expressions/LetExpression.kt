package sigma.expressions

import sigma.Thunk
import sigma.values.tables.LoopedScope
import sigma.parser.antlr.SigmaParser.LetExpressionContext
import sigma.values.tables.Scope
import sigma.types.Type

data class LetExpression(
    val declarations: List<Declaration>,
    val result: Expression,
) : Expression {
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

    override fun inferType(): Type = result.inferType()

    override fun evaluate(
        context: Scope,
    ): Thunk {
        val scope = LoopedScope(
            context = context,
            declarations = declarations.associate {
                it.name to it.value
            },
        )

        return result.evaluate(
            context = scope,
        )
    }
}
