package sigma

import sigma.parser.antlr.SigmaParser.TypeExpressionContext

sealed interface TypeExpression {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = TypeReference(
            referee = Symbol.of(ctx.reference().referee.text),
        )
    }

    fun evaluate(
        context: TypeContext,
    ): Type?
}
