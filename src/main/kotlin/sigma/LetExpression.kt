package sigma

import sigma.parser.antlr.SigmaParser.LetExpressionContext

data class LetExpression(
    val scope: TableConstructor,
    val result: Expression,
) : Expression {
    companion object {
        fun build(
            let: LetExpressionContext,
        ): LetExpression = LetExpression(
            scope = TableConstructor.build(let.table()),
            result = Expression.build(let.result),
        )
    }

    override fun dump(): String = "(let expression)"

    override fun evaluate(
        context: Table,
    ): Value {
        val scope = LoopedAssociativeTable(
            context = context,
            associations = scope.construct(
                environment = context,
            ),
        )

        return result.evaluate(
            context = scope.environment,
        )
    }
}
