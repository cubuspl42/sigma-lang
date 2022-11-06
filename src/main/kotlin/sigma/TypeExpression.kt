package sigma

import sigma.expressions.Term
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.types.Type
import sigma.values.Symbol

abstract class TypeExpression : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = TypeReference(
            referee = Symbol.of(ctx.reference().referee.text),
        )

        fun parse(source: String): TypeExpression {
            TODO()
        }
    }

    abstract fun evaluate(
        typeScope: StaticTypeScope,
    ): Type
}
