package com.github.cubuspl42.sigmaLang.analyzer.semantics

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.EvaluationOutcome
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser

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

    fun evaluateResult(): EvaluationOutcome<Value> {
        val result = module.rootNamespaceDefinition.getDefinition(
            name = Symbol.of("main")
        )!!

        return result.valueThunk.evaluateInitial()
    }
}
