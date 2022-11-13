package sigma

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.expressions.Expression
import sigma.expressions.Term
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.TypeExpressionContext
import sigma.types.Type
import sigma.values.Symbol

// Thought: Move to a sub-package? And clean up subclasses locations.
abstract class TypeExpression : Term() {
    companion object {
        fun build(
            ctx: TypeExpressionContext,
        ): TypeExpression = TypeReference(
            referee = Symbol.of(ctx.reference().referee.text),
        )

        fun parse(
            source: String,
        ): TypeExpression {
            val sourceName = "__type_expression__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return TypeExpression.build(parser.typeExpression())
        }
    }

    abstract fun evaluate(
        typeScope: StaticTypeScope,
    ): Type
}
