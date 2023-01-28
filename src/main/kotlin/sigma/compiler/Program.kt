package sigma.compiler

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.BuiltinTypeScope
import sigma.syntax.expressions.Expression
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.types.Type
import sigma.values.Value

class Program(
    private val prelude: Prelude,
    private val root: Expression,
) {
    companion object {
        internal fun buildParser(
            sourceName: String,
            source: String,
        ): SigmaParser {
            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)

            return SigmaParser(tokenStream)
        }
    }

    fun inferType(): Type = root.inferType(
        typeScope = BuiltinTypeScope,
        valueScope = prelude.valueScope,
    )

    fun evaluate(): Value {
        root.validate(
            typeScope = BuiltinTypeScope,
            valueScope = prelude.valueScope,
        )

        val result = root.evaluate(
            scope = prelude.scope,
        )

        return result.toEvaluatedValue
    }
}
