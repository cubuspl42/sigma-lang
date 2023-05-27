package sigma.semantics

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.evaluation.values.Symbol
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.evaluation.values.Value

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

    fun validate() {

    }

    fun evaluateResult(): Value {
        val result = module.getGlobalDefinition(
            name = Symbol.of("main")
        )!!

        return result.definerThunk.toEvaluatedValue
    }
}
