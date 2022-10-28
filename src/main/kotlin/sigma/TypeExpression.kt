package sigma

import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.types.Type
import sigma.values.Symbol

interface TypeExpression {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = TypeReference(
            referee = Symbol.of(ctx.reference().referee.text),
        )
    }

    fun evaluate(
        context: StaticScope,
    ): Type
}
