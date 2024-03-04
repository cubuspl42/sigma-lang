package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext

sealed interface Term {
    abstract  class Builder<TContext : ParserRuleContext, TTerm : Term> {
        fun parse(source: String): TTerm {
            val sourceName = "__main__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(extract(parser))
        }

        abstract fun build(ctx: TContext): TTerm

        abstract fun extract(parser: SigmaParser): TContext
    }
}
