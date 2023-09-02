package com.github.cubuspl42.sigmaLang.analyzer.syntax

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext

abstract class SourceTerm {
    companion object {
        fun <T : SourceTerm, R : ParserRuleContext> parse(
            source: String,
            sourceName: String,
            rule: (SigmaParser) -> R,
            build: (R) -> T,
        ): T {
            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(rule(parser))
        }
    }

    abstract val location: SourceLocation
}
