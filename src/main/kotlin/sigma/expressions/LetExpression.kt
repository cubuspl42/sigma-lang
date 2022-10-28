package sigma.expressions

import sigma.values.tables.LoopedScope
import sigma.values.tables.Table
import sigma.values.Value
import sigma.parser.antlr.SigmaParser.LetExpressionContext

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

    override fun evaluate(
        context: Table,
    ): Value {
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
