package sigma

import sigma.parser.antlr.SigmaParser.LetExpressionContext

data class LetExpression(
    val scopeConstructor: ScopeConstructor,
    val result: Expression,
) : Expression {
    companion object {
        fun build(
            let: LetExpressionContext,
        ): LetExpression = LetExpression(
            scopeConstructor = ScopeConstructor.build(let.scope()),
            result = Expression.build(let.result),
        )
    }

    override fun evaluate(
        scope: Scope,
    ): Value = result.evaluate(
        scope = scopeConstructor.link(parent = scope),
    )

    override fun dump(): String = "(let expression)"
}
