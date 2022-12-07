package sigma

import getResourceAsText
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.expressions.Expression
import sigma.expressions.LetExpression
import sigma.expressions.LocalScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.values.Value

object Program {
    fun evaluate(
        sourceName: String,
        source: String,
    ): Value {
        val preludeSource = getResourceAsText("prelude.sigma") ?: throw RuntimeException("Couldn't load prelude")

        val prelude = LocalScope.parse(
            sourceName = "prelude",
            source = preludeSource,
        )

        val preludeValueScope = prelude.evaluateStatically(
            typeScope = BuiltinTypeScope,
            valueScope = BuiltinScope,
        )

        val preludeScope = prelude.evaluateDynamically(
            scope = BuiltinScope,
        )

        val program = parse(
            sourceName = sourceName,
            source = source,
        )

        program.validate(
            typeScope = BuiltinTypeScope,
            valueScope = preludeValueScope,
        )

        val result = program.evaluate(
            scope = preludeScope,
        )

        return result.toEvaluatedValue
    }

    fun parse(
        sourceName: String,
        source: String,
    ): Expression = LetExpression.build(
        ctx = buildParser(
            sourceName = sourceName,
            source = source,
        ).program().letExpression(),
    )

    internal fun buildParser(
        sourceName: String,
        source: String,
    ): SigmaParser {
        val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
        val tokenStream = CommonTokenStream(lexer)

        return SigmaParser(tokenStream)
    }
}
