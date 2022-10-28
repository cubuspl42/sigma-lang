package sigma.expressions

import sigma.TypeContext
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.types.Type
import sigma.values.Symbol

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
