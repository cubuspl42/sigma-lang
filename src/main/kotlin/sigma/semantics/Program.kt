package sigma.semantics

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser

class Program internal constructor(
    private val module: Module,
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

    val errors: Set<SemanticError> by lazy { module.errors }

    fun evaluateResult(): Value {
        val result = module.getConstantDefinition(
            name = Symbol.of("main")
        )!!

        return result.definedValue.toEvaluatedValue
    }
}
